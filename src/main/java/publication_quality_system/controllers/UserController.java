package publication_quality_system.controllers;

import org.springframework.web.bind.annotation.*;
import publication_quality_system.base.BaseCrudController;
import publication_quality_system.dtos.UserDto;
import publication_quality_system.services.UserService;

@RestController
@RequestMapping("/api/lab-members/users")
public class UserController extends BaseCrudController<UserDto, Long> {

    public UserController(UserService userService) {
        super(userService);
    }

}
