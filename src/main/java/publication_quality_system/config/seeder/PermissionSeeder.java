package publication_quality_system.config.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.base.BaseDataSeeder;
import publication_quality_system.entities.Permission;
import publication_quality_system.enums.PermissionName;
import publication_quality_system.repositories.PermissionRepository;

@Component
@RequiredArgsConstructor
public class PermissionSeeder implements BaseDataSeeder {

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void seed() {
        for (PermissionName name : PermissionName.values()) {
            String permissionName = name.name();
            if (permissionRepository.findByName(permissionName).isEmpty()) {
                Permission permission = new Permission();
                permission.setName(permissionName);
                permission.setDescription("Permission for " + permissionName);
                permission.setCreatedBy("SYSTEM");
                permission.setUpdatedBy("SYSTEM");
                permissionRepository.save(permission);
            }
        }
    }

    @Override
    public int getOrder() {
        return 1; // Permissions must be seeded before roles
    }
}
