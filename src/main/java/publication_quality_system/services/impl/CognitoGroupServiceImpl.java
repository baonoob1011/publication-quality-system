package publication_quality_system.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import publication_quality_system.exceptions.AppException;
import publication_quality_system.exceptions.RoleErrorCode;
import publication_quality_system.services.CognitoGroupService;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRemoveUserFromGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeleteGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GroupType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResourceNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CognitoGroupServiceImpl implements CognitoGroupService {

    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.cognito.user-pool-id:}")
    private String userPoolId;

    @Override
    public void ensureGroupExists(String groupName) {
        if (!groupExists(groupName)) {
            createGroup(groupName, "Auto-created group for role " + groupName);
        }
    }

    @Override
    public void createGroup(String groupName, String description) {
        if (isBlank(groupName) || isBlank(userPoolId)) {
            return;
        }
        if (groupExists(groupName)) {
            return;
        }

        try {
            cognitoClient.createGroup(CreateGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName)
                    .description(description)
                    .build());
        } catch (CognitoIdentityProviderException exception) {
            throw toSyncException(exception);
        }
    }

    @Override
    public void updateGroup(String oldGroupName, String newGroupName, String description) {
        if (oldGroupName.equals(newGroupName)) {
            ensureGroupExists(newGroupName);
            return;
        }
        createGroup(newGroupName, description);
        deleteGroup(oldGroupName);
    }

    @Override
    public void deleteGroup(String groupName) {
        if (isBlank(groupName) || isBlank(userPoolId)) {
            return;
        }

        try {
            cognitoClient.deleteGroup(DeleteGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName)
                    .build());
        } catch (ResourceNotFoundException exception) {
            log.warn("Cognito group does not exist: {}", groupName);
        } catch (CognitoIdentityProviderException exception) {
            throw toSyncException(exception);
        }
    }

    @Override
    public void addUserToGroup(String username, String groupName) {
        if (isBlank(username) || isBlank(groupName) || isBlank(userPoolId)) {
            return;
        }

        try {
            cognitoClient.adminAddUserToGroup(AdminAddUserToGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .groupName(groupName)
                    .build());
        } catch (CognitoIdentityProviderException exception) {
            throw toSyncException(exception);
        }
    }

    @Override
    public void removeUserFromGroup(String username, String groupName) {
        if (isBlank(username) || isBlank(groupName) || isBlank(userPoolId)) {
            return;
        }

        try {
            cognitoClient.adminRemoveUserFromGroup(AdminRemoveUserFromGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .groupName(groupName)
                    .build());
        } catch (ResourceNotFoundException | UserNotFoundException exception) {
            log.warn("Cognito user or group not found while removing user {} from group {}", username, groupName);
        } catch (CognitoIdentityProviderException exception) {
            throw toSyncException(exception);
        }
    }

    @Override
    public Set<String> getUserGroups(String username) {
        if (isBlank(username) || isBlank(userPoolId)) {
            return Set.of();
        }

        try {
            return cognitoClient.adminListGroupsForUser(AdminListGroupsForUserRequest.builder()
                            .userPoolId(userPoolId)
                            .username(username)
                            .build())
                    .groups()
                    .stream()
                    .map(GroupType::groupName)
                    .collect(Collectors.toSet());
        } catch (CognitoIdentityProviderException exception) {
            throw toSyncException(exception);
        }
    }

    private boolean groupExists(String groupName) {
        try {
            cognitoClient.getGroup(GetGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName)
                    .build());
            return true;
        } catch (ResourceNotFoundException exception) {
            return false;
        } catch (CognitoIdentityProviderException exception) {
            throw toSyncException(exception);
        }
    }

    private AppException toSyncException(CognitoIdentityProviderException exception) {
        String errorCode = exception.awsErrorDetails() != null ? exception.awsErrorDetails().errorCode() : "UNKNOWN";
        String errorMessage = exception.awsErrorDetails() != null ? exception.awsErrorDetails().errorMessage() : exception.getMessage();
        log.error("Cognito group sync failed. code={}, message={}, status={}", errorCode, errorMessage, exception.statusCode());
        return new AppException(RoleErrorCode.COGNITO_GROUP_SYNC_FAILED, "Cognito group sync failed [" + errorCode + "]: " + errorMessage);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
