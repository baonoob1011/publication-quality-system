package publication_quality_system.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.base.BaseDataSeeder;
import publication_quality_system.entities.Permission;
import publication_quality_system.entities.Role;
import publication_quality_system.enums.PermissionName;
import publication_quality_system.enums.RoleName;
import publication_quality_system.repositories.PermissionRepository;
import publication_quality_system.repositories.RoleRepository;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResourceNotFoundException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleSeeder implements BaseDataSeeder {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final JdbcTemplate jdbcTemplate;
    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.cognito.user-pool-id:}")
    private String userPoolId;

    @Override
    @Transactional
    public void seed() {
        fixRoleNameConstraint();

        for (RoleName roleName : RoleName.values()) {
            Role role = roleRepository.findByName(roleName.name()).orElse(null);
            if (role == null) {
                role = new Role();
                role.setName(roleName.name());
                role.setDescription("Role for " + roleName.name());
                role.setCreatedBy("SYSTEM");
                role.setUpdatedBy("SYSTEM");
                role = roleRepository.save(role);
            }

            Set<PermissionName> allowedPermissions = getPermissionsForRole(roleName);
            Set<Permission> permissionsToAssign = allowedPermissions.stream()
                    .map(name -> permissionRepository.findByName(name.name())
                            .orElseThrow(() -> new IllegalStateException("Permission not found: " + name)))
                    .collect(Collectors.toSet());

            role.setPermissions(permissionsToAssign);
            roleRepository.save(role);
        }

        syncRolesToCognitoGroups();
    }

    @Override
    public int getOrder() {
        return 2; // Roles must be seeded after permissions
    }

    private void fixRoleNameConstraint() {
        try {
            jdbcTemplate.execute("""
                    DO $$
                    DECLARE
                        constraint_record RECORD;
                    BEGIN
                        IF to_regclass('public.roles') IS NOT NULL THEN
                            FOR constraint_record IN
                                SELECT conname
                                FROM pg_constraint
                                WHERE conrelid = 'public.roles'::regclass
                                  AND contype = 'c'
                                  AND pg_get_constraintdef(oid) LIKE '%name%'
                            LOOP
                                EXECUTE format('ALTER TABLE roles DROP CONSTRAINT IF EXISTS %I', constraint_record.conname);
                            END LOOP;
                        END IF;
                    END $$;
                    """);
            log.info("Role name constraints fixed successfully");
        } catch (Exception e) {
            log.warn("Skip fixing role name constraints: {}", e.getMessage());
        }
    }

    private void syncRolesToCognitoGroups() {
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

    private Set<PermissionName> getPermissionsForRole(RoleName roleName) {
        if (roleName == RoleName.ADMIN) {
            return new HashSet<>(Arrays.asList(PermissionName.values()));
        }

        Set<PermissionName> permissions = new HashSet<>();

        if (roleName == RoleName.LAB_LEADER) {
            permissions.addAll(Arrays.asList(
                    PermissionName.USER_READ, PermissionName.ROLE_ASSIGN,
                    PermissionName.ROLE_READ,
                    PermissionName.FILE_UPLOAD, PermissionName.FILE_DELETE,
                    PermissionName.LAB_MEMBER_CREATE, PermissionName.LAB_MEMBER_READ,
                    PermissionName.LAB_MEMBER_UPDATE, PermissionName.LAB_MEMBER_DELETE,
                    PermissionName.RESEARCH_GROUP_CREATE, PermissionName.RESEARCH_GROUP_READ,
                    PermissionName.RESEARCH_GROUP_UPDATE, PermissionName.RESEARCH_GROUP_DELETE,
                    PermissionName.RESEARCH_GROUP_MEMBER_MANAGE,
                    PermissionName.RESEARCH_PROFILE_CREATE, PermissionName.RESEARCH_PROFILE_READ,
                    PermissionName.RESEARCH_PROFILE_UPDATE, PermissionName.RESEARCH_PROFILE_DELETE,
                    PermissionName.PAPER_CREATE, PermissionName.PAPER_READ_OWN, PermissionName.PAPER_READ_ALL,
                    PermissionName.PAPER_UPDATE_OWN, PermissionName.PAPER_UPDATE_ALL,
                    PermissionName.PAPER_DELETE_OWN, PermissionName.PAPER_DELETE_ALL,
                    PermissionName.PAPER_SUBMIT_INTERNAL_REVIEW,
                    PermissionName.PAPER_VERSION_UPLOAD, PermissionName.PAPER_VERSION_READ,
                    PermissionName.PAPER_VERSION_COMPARE, PermissionName.PAPER_VERSION_DELETE,
                    PermissionName.QUALITY_CHECK_RUN, PermissionName.QUALITY_REPORT_READ,
                    PermissionName.INTEGRITY_CHECK_RUN, PermissionName.INTEGRITY_REPORT_READ_OWN,
                    PermissionName.INTEGRITY_REPORT_READ_ALL, PermissionName.INTEGRITY_REPORT_APPROVE,
                    PermissionName.REVIEW_CREATE, PermissionName.REVIEW_READ_ASSIGNED,
                    PermissionName.REVIEW_READ_ALL, PermissionName.REVIEW_ASSIGN,
                    PermissionName.REVIEW_DECISION_SUBMIT,
                    PermissionName.VENUE_RECOMMEND,
                    PermissionName.SUBMISSION_CREATE, PermissionName.SUBMISSION_UPDATE,
                    PermissionName.SUBMISSION_TRACK, PermissionName.SUBMISSION_APPROVE,
                    PermissionName.REVIEWER_RESPONSE_CREATE, PermissionName.REVIEWER_RESPONSE_READ,
                    PermissionName.NOTIFICATION_READ, PermissionName.NOTIFICATION_MANAGE,
                    PermissionName.ANALYTICS_READ_OWN, PermissionName.ANALYTICS_READ_LAB,
                    PermissionName.KNOWLEDGE_BASE_CREATE, PermissionName.KNOWLEDGE_BASE_READ,
                    PermissionName.KNOWLEDGE_BASE_UPDATE));
        } else if (roleName == RoleName.SENIOR_RESEARCHER) {
            permissions.addAll(Arrays.asList(
                    PermissionName.FILE_UPLOAD,
                    PermissionName.LAB_MEMBER_READ,
                    PermissionName.RESEARCH_GROUP_READ, PermissionName.RESEARCH_PROFILE_READ,
                    PermissionName.PAPER_CREATE, PermissionName.PAPER_READ_OWN,
                    PermissionName.PAPER_UPDATE_OWN, PermissionName.PAPER_DELETE_OWN,
                    PermissionName.PAPER_SUBMIT_INTERNAL_REVIEW,
                    PermissionName.PAPER_VERSION_UPLOAD, PermissionName.PAPER_VERSION_READ,
                    PermissionName.PAPER_VERSION_COMPARE, PermissionName.PAPER_VERSION_DELETE,
                    PermissionName.QUALITY_CHECK_RUN, PermissionName.QUALITY_REPORT_READ,
                    PermissionName.INTEGRITY_CHECK_RUN, PermissionName.INTEGRITY_REPORT_READ_OWN,
                    PermissionName.INTEGRITY_REPORT_APPROVE,
                    PermissionName.REVIEW_CREATE, PermissionName.REVIEW_READ_ASSIGNED,
                    PermissionName.REVIEW_DECISION_SUBMIT,
                    PermissionName.VENUE_RECOMMEND,
                    PermissionName.SUBMISSION_CREATE, PermissionName.SUBMISSION_UPDATE,
                    PermissionName.SUBMISSION_TRACK, PermissionName.SUBMISSION_APPROVE,
                    PermissionName.REVIEWER_RESPONSE_CREATE, PermissionName.REVIEWER_RESPONSE_READ,
                    PermissionName.NOTIFICATION_READ,
                    PermissionName.ANALYTICS_READ_OWN,
                    PermissionName.KNOWLEDGE_BASE_CREATE, PermissionName.KNOWLEDGE_BASE_READ,
                    PermissionName.KNOWLEDGE_BASE_UPDATE));
        } else if (roleName == RoleName.RESEARCHER) {
            permissions.addAll(Arrays.asList(
                    PermissionName.FILE_UPLOAD,
                    PermissionName.LAB_MEMBER_READ,
                    PermissionName.RESEARCH_GROUP_READ, PermissionName.RESEARCH_PROFILE_READ,
                    PermissionName.PAPER_CREATE, PermissionName.PAPER_READ_OWN,
                    PermissionName.PAPER_UPDATE_OWN, PermissionName.PAPER_DELETE_OWN,
                    PermissionName.PAPER_SUBMIT_INTERNAL_REVIEW,
                    PermissionName.PAPER_VERSION_UPLOAD, PermissionName.PAPER_VERSION_READ,
                    PermissionName.PAPER_VERSION_COMPARE, PermissionName.PAPER_VERSION_DELETE,
                    PermissionName.QUALITY_CHECK_RUN, PermissionName.QUALITY_REPORT_READ,
                    PermissionName.INTEGRITY_CHECK_RUN, PermissionName.INTEGRITY_REPORT_READ_OWN,
                    PermissionName.VENUE_RECOMMEND,
                    PermissionName.SUBMISSION_CREATE, PermissionName.SUBMISSION_UPDATE,
                    PermissionName.SUBMISSION_TRACK,
                    PermissionName.REVIEWER_RESPONSE_CREATE, PermissionName.REVIEWER_RESPONSE_READ,
                    PermissionName.NOTIFICATION_READ,
                    PermissionName.ANALYTICS_READ_OWN,
                    PermissionName.KNOWLEDGE_BASE_READ));
        } else if (roleName == RoleName.AI_QUALITY_ASSISTANT) {
            permissions.addAll(Arrays.asList(
                    PermissionName.PAPER_READ_ALL,
                    PermissionName.PAPER_VERSION_READ, PermissionName.PAPER_VERSION_COMPARE,
                    PermissionName.QUALITY_CHECK_RUN, PermissionName.QUALITY_REPORT_READ,
                    PermissionName.INTEGRITY_CHECK_RUN, PermissionName.INTEGRITY_REPORT_READ_ALL,
                    PermissionName.VENUE_RECOMMEND,
                    PermissionName.SUBMISSION_TRACK,
                    PermissionName.NOTIFICATION_READ,
                    PermissionName.KNOWLEDGE_BASE_READ));
        }

        return permissions;
    }
}
