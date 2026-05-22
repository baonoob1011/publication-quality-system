package publication_quality_system.services;

import publication_quality_system.base.BaseCrudService;
import publication_quality_system.dtos.RoleDto;

public interface RoleService extends BaseCrudService<RoleDto, Long> {
    void assignRole (Long userId, Long roleId);
}
