package publication_quality_system.lab_member.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import publication_quality_system.base.BaseCrudController;
import publication_quality_system.lab_member.dtos.RoleDto;
import publication_quality_system.lab_member.services.RoleService;

@RestController
@RequestMapping("/api/lab-members/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController extends BaseCrudController<RoleDto, Long> {
    public RoleController(RoleService roleService) {
        super(roleService);
    }
}
