package publication_quality_system.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import publication_quality_system.base.BaseDataSeeder;
import publication_quality_system.entities.Role;
import publication_quality_system.entities.User;
import publication_quality_system.enums.RoleName;
import publication_quality_system.repositories.RoleRepository;
import publication_quality_system.repositories.UserRepository;
import publication_quality_system.services.CognitoUserService;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements BaseDataSeeder {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String SYSTEM_USER = "SYSTEM";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CognitoUserService cognitoUserService;
    private final TransactionTemplate transactionTemplate;

    @Value("${app.admin.full-name:}")
    private String adminFullName;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    public void seed() {
        adminFullName = normalize(adminFullName);
        adminEmail = normalizeEmail(adminEmail);
        adminPassword = normalize(adminPassword);

        log.info("Admin config loaded: fullNamePresent={}, emailPresent={}, passwordPresent={}",
                !isBlank(adminFullName),
                !isBlank(adminEmail),
                !isBlank(adminPassword));

        if (isAdminConfigInvalid()) {
            log.warn("Default admin account was not seeded because admin full name, email, or password is missing");
            return;
        }

        if (isPasswordWeak()) {
            log.warn("Default admin account was not seeded because admin password is too weak");
            return;
        }

        Role adminRole = findAdminRole();
        if (adminRole == null) {
            log.error("Default admin account was not seeded because ADMIN role does not exist");
            return;
        }

        boolean dbExists = adminExistsInDatabase();
        boolean cognitoExists = cognitoUserService.existsByEmail(adminEmail);

        if (dbExists && cognitoExists) {
            log.info("Default admin account already synchronized");
            return;
        }

        if (!cognitoExists) {
            boolean cognitoSynced = cognitoUserService.ensureDefaultAdminUser(adminEmail, adminPassword);
            if (!cognitoSynced) {
                log.warn("Default admin Cognito sync failed");
                return;
            }
        }

        if (!dbExists) {
            try {
                createAdminInDatabase(adminRole);
            } catch (RuntimeException exception) {
                log.error("Default admin account was not seeded because database save failed", exception);
                return;
            }
        }

        log.info("Default admin account synchronized successfully");
    }

    @Override
    public int getOrder() {
        return 3;
    }

    private boolean isAdminConfigInvalid() {
        return isBlank(adminFullName) || isBlank(adminEmail) || isBlank(adminPassword);
    }

    private boolean isPasswordWeak() {
        return adminPassword.length() < MIN_PASSWORD_LENGTH;
    }

    private boolean adminExistsInDatabase() {
        return userRepository.existsByEmail(adminEmail);
    }

    private Role findAdminRole() {
        return roleRepository.findByName(RoleName.ADMIN.name()).orElse(null);
    }

    private void createAdminInDatabase(Role adminRole) {
        transactionTemplate.executeWithoutResult(status -> {
            if (adminExistsInDatabase()) {
                log.info("Default admin account already exists in database");
                return;
            }

            User admin = new User();
            admin.setFullName(adminFullName);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setDeleted(false);
            admin.setRoles(Set.of(adminRole));
            admin.setCreatedBy(SYSTEM_USER);
            admin.setUpdatedBy(SYSTEM_USER);

            userRepository.save(admin);
        });
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeEmail(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }
}
