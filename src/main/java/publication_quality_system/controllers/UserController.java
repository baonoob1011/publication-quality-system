package publication_quality_system.lab_member.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import publication_quality_system.base.BaseCrudController;
import publication_quality_system.base.BaseResponse;
import publication_quality_system.lab_member.dtos.UserDto;
import publication_quality_system.lab_member.services.UserService;

@RestController
@RequestMapping("/api/lab-members/users")
public class UserController extends BaseCrudController<UserDto, Long> {

    private final UserService userService;

    public UserController(UserService userService) {
        super(userService);
        this.userService = userService;
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_LEADER')")
    public ResponseEntity<BaseResponse<Void>> assignRole(@PathVariable Long userId, @PathVariable Long roleId) {
        userService.assignRole(userId, roleId);
        return success(null, "Role assigned successfully");
    }

    // Overriding create to add custom security if needed
    @Override
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_LEADER')")
    public ResponseEntity<BaseResponse<UserDto>> create(@RequestBody UserDto dto) {
        return super.create(dto);
    }
}
