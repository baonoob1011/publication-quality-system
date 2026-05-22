package publication_quality_system.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import publication_quality_system.base.BaseCrudController;
import publication_quality_system.base.BaseResponse;
import publication_quality_system.dtos.ResearchProfileDto;
import publication_quality_system.services.ResearchProfileService;

@RestController
@RequestMapping("/api/lab-members/profiles")
public class ResearchProfileController extends BaseCrudController<ResearchProfileDto, Long> {

    private final ResearchProfileService profileService;

    public ResearchProfileController(ResearchProfileService profileService) {
        super(profileService);
        this.profileService = profileService;
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('RESEARCH_PROFILE_READ')")
    public ResponseEntity<BaseResponse<ResearchProfileDto>> getByUserId(@PathVariable Long userId) {
        ResearchProfileDto profile = profileService.getByUserId(userId);
        return success(profile, "Profile retrieved successfully");
    }
}
