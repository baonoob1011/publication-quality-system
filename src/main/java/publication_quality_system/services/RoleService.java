package publication_quality_system.services;

import org.springframework.data.domain.Pageable;
import publication_quality_system.dtos.CreateRoleDto;
import publication_quality_system.dtos.RoleDto;
import publication_quality_system.dtos.UpdateRoleDto;
import publication_quality_system.dtos.UpdateUserRolesDto;
import publication_quality_system.dtos.UserRoleDto;

import java.util.List;

public interface RoleService {
    RoleDto createRole(CreateRoleDto dto);

    RoleDto updateRole(Long roleId, UpdateRoleDto dto);

    void deleteRole(Long roleId);

    RoleDto getRoleById(Long roleId);

    List<RoleDto> getAllRoles(Pageable pageable);

    List<UserRoleDto> getUserRoles(Long userId);

    List<UserRoleDto> assignRoleToUser(Long userId, Long roleId);

    List<UserRoleDto> removeRoleFromUser(Long userId, Long roleId);

    List<UserRoleDto> replaceUserRoles(Long userId, UpdateUserRolesDto dto);
}
