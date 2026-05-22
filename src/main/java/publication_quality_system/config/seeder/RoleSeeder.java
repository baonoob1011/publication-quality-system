package publication_quality_system.config.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.base.BaseDataSeeder;
import publication_quality_system.entities.Permission;
import publication_quality_system.entities.Role;
import publication_quality_system.enums.PermissionName;
import publication_quality_system.enums.RoleName;
import publication_quality_system.repositories.PermissionRepository;
import publication_quality_system.repositories.RoleRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements BaseDataSeeder {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void seed() {
        for (RoleName roleName : RoleName.values()) {
            Role role = roleRepository.findByName(roleName).orElse(null);
            if (role == null) {
                role = new Role();
                role.setName(roleName);
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
    }

    @Override
    public int getOrder() {
        return 2; // Roles must be seeded after permissions
    }

    private Set<PermissionName> getPermissionsForRole(RoleName roleName) {
        if (roleName == RoleName.ADMIN) {
            return new HashSet<>(Arrays.asList(PermissionName.values()));
        }

        Set<PermissionName> permissions = new HashSet<>();

        if (roleName == RoleName.LAB_LEADER) {
            permissions.addAll(Arrays.asList(
                    PermissionName.USER_READ, PermissionName.ROLE_ASSIGN,
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
