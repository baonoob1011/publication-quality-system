package publication_quality_system.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.base.BaseDataSeeder;
import publication_quality_system.entities.Role;
import publication_quality_system.entities.User;
import publication_quality_system.enums.RoleName;
import publication_quality_system.repositories.RoleRepository;
import publication_quality_system.repositories.UserRepository;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements BaseDataSeeder {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:}")
    private String adminUsername;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    @Transactional
    public void seed() {
        if (isBlank(adminUsername) || isBlank(adminEmail) || isBlank(adminPassword)) {
            log.warn("Default admin account was not seeded because admin username, email, or password is missing");
            return;
        }

        if (adminPassword.length() < MIN_PASSWORD_LENGTH) {
            log.warn("Default admin account was not seeded because admin password is too weak");
            return;
        }

        if (userRepository.findByUsername(adminUsername).isPresent()) {
            log.info("Default admin account already exists");
            return;
        }

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            log.info("Default admin email already exists");
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ADMIN.name()).orElse(null);
        if (adminRole == null) {
            log.error("Default admin account was not seeded because ADMIN role does not exist");
            return;
        }

        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setDeleted(false);
        admin.setRoles(Set.of(adminRole));
        admin.setCreatedBy("SYSTEM");
        admin.setUpdatedBy("SYSTEM");

        userRepository.save(admin);
        log.info("Default admin account seeded successfully");
    }

    @Override
    public int getOrder() {
        return 3;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
