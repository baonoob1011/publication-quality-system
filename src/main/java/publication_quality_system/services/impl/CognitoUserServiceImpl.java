package publication_quality_system.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import publication_quality_system.properties.CognitoProperties;
import publication_quality_system.services.CognitoUserService;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.MessageActionType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResourceNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CognitoUserServiceImpl implements CognitoUserService {

    private final CognitoIdentityProviderClient cognitoClient;
    private final CognitoProperties cognitoProperties;

    @Override
    public boolean existsByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);

        if (isBlank(normalizedEmail)) {
            log.warn("Default admin Cognito user existence was not checked because email is missing");
            return false;
        }

        if (isBlank(cognitoProperties.getUserPoolId())) {
            log.warn("Default admin Cognito user existence was not checked because aws.cognito.user-pool-id is missing");
            return false;
        }

        try {
            return findUserByUsername(normalizedEmail).isPresent() || findUserByEmail(normalizedEmail).isPresent();
        } catch (CognitoIdentityProviderException exception) {
            logCognitoError("Default admin Cognito user existence check failed", exception);
            return false;
        } catch (SdkClientException exception) {
            log.error("Default admin Cognito user existence check failed because AWS SDK client could not complete the request", exception);
            return false;
        }
    }

    @Override
    public boolean ensureDefaultAdminUser(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedPassword = normalizePassword(password);

        if (!hasValidAdminInput(normalizedEmail, normalizedPassword)) {
            log.warn("Default admin Cognito user was not synced because email or password is missing");
            return false;
        }

        if (isBlank(cognitoProperties.getUserPoolId())) {
            log.warn("Default admin Cognito user was not synced because aws.cognito.user-pool-id is missing");
            return false;
        }

        try {
            String cognitoUsername = findUserByUsername(normalizedEmail)
                    .or(() -> findUserByEmail(normalizedEmail))
                    .map(UserType::username)
                    .orElseGet(() -> createUser(normalizedEmail));

            if (isBlank(cognitoUsername)) {
                return false;
            }

            verifyEmail(cognitoUsername, normalizedEmail);
            setPermanentPassword(cognitoUsername, normalizedPassword);
            if (!isConfirmed(cognitoUsername)) {
                log.warn("Default admin Cognito user is not login-ready after password sync: {}", cognitoUsername);
                return false;
            }
            addToAdminGroupIfExists(cognitoUsername);
            log.info("Default admin Cognito user is ready: {}", cognitoUsername);
            return true;
        } catch (CognitoIdentityProviderException exception) {
            logCognitoError("Default admin Cognito user sync failed", exception);
            return false;
        } catch (SdkClientException exception) {
            log.error("Default admin Cognito user sync failed because AWS SDK client could not complete the request", exception);
            return false;
        }
    }

    private Optional<UserType> findUserByUsername(String username) {
        if (isBlank(username)) {
            return Optional.empty();
        }

        try {
            cognitoClient.adminGetUser(AdminGetUserRequest.builder()
                    .userPoolId(cognitoProperties.getUserPoolId())
                    .username(username)
                    .build());
            return Optional.of(UserType.builder().username(username).build());
        } catch (UserNotFoundException exception) {
            return Optional.empty();
        }
    }

    private Optional<UserType> findUserByEmail(String email) {
        if (isBlank(email)) {
            return Optional.empty();
        }

        return cognitoClient.listUsers(ListUsersRequest.builder()
                        .userPoolId(cognitoProperties.getUserPoolId())
                        .filter("email = \"" + email.replace("\"", "\\\"") + "\"")
                        .limit(1)
                        .build())
                .users()
                .stream()
                .findFirst();
    }

    private String createUser(String email) {
        if (isBlank(email)) {
            log.warn("Default admin Cognito user was not created because email is missing");
            return null;
        }

        log.info("Creating Cognito admin user with email={}", email);
        try {
            return cognitoClient.adminCreateUser(AdminCreateUserRequest.builder()
                            .userPoolId(cognitoProperties.getUserPoolId())
                            .username(email)
                            .userAttributes(
                                    AttributeType.builder().name("email").value(email).build(),
                                    AttributeType.builder().name("email_verified").value("true").build())
                            .messageAction(MessageActionType.SUPPRESS)
                            .build())
                    .user()
                    .username();
        } catch (UsernameExistsException exception) {
            log.info("Default admin Cognito user already exists: {}", email);
            return email;
        }
    }

    private void setPermanentPassword(String username, String password) {
        if (isBlank(username)) {
            return;
        }

        cognitoClient.adminSetUserPassword(AdminSetUserPasswordRequest.builder()
                .userPoolId(cognitoProperties.getUserPoolId())
                .username(username)
                .password(password)
                .permanent(true)
                .build());
    }

    private boolean isConfirmed(String username) {
        if (isBlank(username)) {
            return false;
        }

        AdminGetUserResponse response = cognitoClient.adminGetUser(AdminGetUserRequest.builder()
                .userPoolId(cognitoProperties.getUserPoolId())
                .username(username)
                .build());

        String status = response.userStatusAsString();
        if ("CONFIRMED".equals(status)) {
            return true;
        }

        log.warn("Default admin Cognito user status is not CONFIRMED: username={}, status={}", username, status);
        return false;
    }

    private void verifyEmail(String username, String email) {
        if (isBlank(username) || isBlank(email)) {
            return;
        }

        cognitoClient.adminUpdateUserAttributes(AdminUpdateUserAttributesRequest.builder()
                .userPoolId(cognitoProperties.getUserPoolId())
                .username(username)
                .userAttributes(
                        AttributeType.builder().name("email").value(email).build(),
                        AttributeType.builder().name("email_verified").value("true").build())
                .build());
    }

    private void addToAdminGroupIfExists(String username) {
        if (isBlank(username)) {
            return;
        }

        String adminGroupName = cognitoProperties.getAdminGroupName();
        if (isBlank(adminGroupName)) {
            return;
        }

        try {
            cognitoClient.getGroup(GetGroupRequest.builder()
                    .userPoolId(cognitoProperties.getUserPoolId())
                    .groupName(adminGroupName)
                    .build());
        } catch (ResourceNotFoundException exception) {
            log.warn("Default admin Cognito user was not added to group because group does not exist: {}", adminGroupName);
            return;
        }

        if (isUserInGroup(username, adminGroupName)) {
            return;
        }

        cognitoClient.adminAddUserToGroup(AdminAddUserToGroupRequest.builder()
                .userPoolId(cognitoProperties.getUserPoolId())
                .username(username)
                .groupName(adminGroupName)
                .build());
    }

    private boolean isUserInGroup(String username, String groupName) {
        if (isBlank(username)) {
            return false;
        }

        return cognitoClient.adminListGroupsForUser(AdminListGroupsForUserRequest.builder()
                        .userPoolId(cognitoProperties.getUserPoolId())
                        .username(username)
                        .build())
                .groups()
                .stream()
                .anyMatch(group -> groupName.equals(group.groupName()));
    }

    private void logCognitoError(String message, CognitoIdentityProviderException exception) {
        String errorCode = exception.awsErrorDetails() != null ? exception.awsErrorDetails().errorCode() : "UNKNOWN";
        String errorMessage = exception.awsErrorDetails() != null ? exception.awsErrorDetails().errorMessage() : exception.getMessage();
        log.error("{}. code={}, message={}, status={}", message, errorCode, errorMessage, exception.statusCode(), exception);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean hasValidAdminInput(String email, String password) {
        return !isBlank(email) && !isBlank(password);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String normalizePassword(String password) {
        return password == null ? null : password.trim();
    }
}
