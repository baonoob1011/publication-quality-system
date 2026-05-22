package publication_quality_system.lab_member.services;

import publication_quality_system.base.BaseCrudService;
import publication_quality_system.lab_member.dtos.UserDto;

public interface UserService extends BaseCrudService<UserDto, Long> {
    void assignRole(Long userId, Long roleId);
}
