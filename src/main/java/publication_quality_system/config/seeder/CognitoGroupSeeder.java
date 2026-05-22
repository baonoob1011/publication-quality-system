package publication_quality_system.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import publication_quality_system.entities.Role;
import publication_quality_system.repositories.RoleRepository;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResourceNotFoundException;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class CognitoGroupSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.cognito.user-pool-id:}")
    private String userPoolId;

    @Override
    public void run(String... args) {
        if (userPoolId == null || userPoolId.isBlank()) {
            log.warn("Cognito groups were not synced because aws.cognito.user-pool-id is missing");
            return;
        }

        for (Role role : roleRepository.findAll()) {
            String groupName = resolveGroupName(role);
            if (groupName == null || groupName.isBlank()) {
                continue;
            }

            try {
                if (cognitoGroupExists(groupName)) {
                    log.info("Cognito group already exists: {}", groupName);
                    continue;
                }

                createCognitoGroup(groupName);
                log.info("Cognito group created successfully: {}", groupName);
            } catch (CognitoIdentityProviderException exception) {
                logCognitoError(groupName, exception);
            }
        }
    }

    private boolean cognitoGroupExists(String groupName) {
        try {
            cognitoClient.getGroup(GetGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName)
                    .build());
            return true;
        } catch (ResourceNotFoundException exception) {
            return false;
        }
    }

    private void createCognitoGroup(String groupName) {
        cognitoClient.createGroup(CreateGroupRequest.builder()
                .userPoolId(userPoolId)
                .groupName(groupName)
                .description("Auto-created group for role " + groupName)
                .build());
    }

    private String resolveGroupName(Role role) {
        if (role == null || role.getName() == null) {
            return null;
        }
        return role.getName().toString();
    }

    private void logCognitoError(String groupName, CognitoIdentityProviderException exception) {
        String errorCode = exception.awsErrorDetails() != null
                ? exception.awsErrorDetails().errorCode()
                : "UNKNOWN";
        String errorMessage = exception.awsErrorDetails() != null
                ? exception.awsErrorDetails().errorMessage()
                : exception.getMessage();
        int statusCode = exception.statusCode();

        log.error(
                "Failed to sync Cognito group {}. code={}, message={}, status={}",
                groupName,
                errorCode,
                errorMessage,
                statusCode
        );
    }
}
