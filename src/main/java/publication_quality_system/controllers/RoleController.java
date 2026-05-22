package publication_quality_system.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import publication_quality_system.base.BaseCrudController;
import publication_quality_system.base.BaseResponse;
import publication_quality_system.dtos.RoleDto;
import publication_quality_system.services.RoleService;

@RestController
@RequestMapping("/api/lab-members/roles")
@PreAuthorize("hasAuthority('ROLE_ASSIGN')")
public class RoleController extends BaseCrudController<RoleDto, Long> {
    private RoleService roleService;
    public RoleController(RoleService roleService) {
        super(roleService);
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
    public ResponseEntity<BaseResponse<Void>> assignRole(@PathVariable Long userId, @PathVariable Long roleId) {
        roleService.assignRole(userId, roleId);
        return success(null, "Role assigned successfully");
    }
}
