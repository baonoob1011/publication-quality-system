package publication_quality_system.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import publication_quality_system.base.BaseController;
import publication_quality_system.base.BaseCrudController;
import publication_quality_system.base.BaseResponse;
import publication_quality_system.dtos.RoleDto;
import publication_quality_system.dtos.UpdateUserRolesDto;
import publication_quality_system.dtos.UserRoleDto;
import publication_quality_system.services.RoleService;
import publication_quality_system.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController extends BaseCrudController<RoleDto, Long> {

    private final RoleService roleService;
    public RoleController(RoleService roleService, RoleService roleService1) {
        super(roleService);
        this.roleService = roleService1;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<BaseResponse<List<RoleDto>>> getAll(Pageable pageable) {
        return success(roleService.getAllRoles(pageable), "Roles retrieved successfully");
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<BaseResponse<List<UserRoleDto>>> getUserRoles(@PathVariable Long userId) {
        return success(roleService.getUserRoles(userId), "User roles retrieved successfully");
    }

    @PostMapping("/users/{userId}/{roleId}")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
    public ResponseEntity<BaseResponse<List<UserRoleDto>>> assignRole(@PathVariable Long userId, @PathVariable Long roleId) {
        return success(roleService.assignRoleToUser(userId, roleId), "Role assigned successfully");
    }

    @DeleteMapping("/users/{userId}/{roleId}")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
    public ResponseEntity<BaseResponse<List<UserRoleDto>>> removeRole(@PathVariable Long userId, @PathVariable Long roleId) {
        return success(roleService.removeRoleFromUser(userId, roleId), "Role removed successfully");
    }

    @PutMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
    public ResponseEntity<BaseResponse<List<UserRoleDto>>> replaceRoles(
            @PathVariable Long userId,
            @RequestBody UpdateUserRolesDto dto) {
        return success(roleService.replaceUserRoles(userId, dto), "User roles updated successfully");
    }
}
