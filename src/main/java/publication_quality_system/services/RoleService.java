package publication_quality_system.services;

import org.springframework.data.domain.Pageable;
import publication_quality_system.base.BaseCrudService;
import publication_quality_system.dtos.RoleDto;
import publication_quality_system.dtos.UpdateUserRolesDto;
import publication_quality_system.dtos.UserRoleDto;

import java.util.List;

public interface RoleService extends BaseCrudService<RoleDto, Long> {

    List<RoleDto> getAllRoles(Pageable pageable);

    List<UserRoleDto> getUserRoles(Long userId);

    List<UserRoleDto> assignRoleToUser(Long userId, Long roleId);

    List<UserRoleDto> removeRoleFromUser(Long userId, Long roleId);

    List<UserRoleDto> replaceUserRoles(Long userId, UpdateUserRolesDto dto);
}
