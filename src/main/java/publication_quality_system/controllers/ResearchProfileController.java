package publication_quality_system.lab_member.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import publication_quality_system.base.BaseCrudController;
import publication_quality_system.base.BaseResponse;
import publication_quality_system.lab_member.dtos.ResearchProfileDto;
import publication_quality_system.lab_member.services.ResearchProfileService;

@RestController
@RequestMapping("/api/lab-members/profiles")
public class ResearchProfileController extends BaseCrudController<ResearchProfileDto, Long> {

    private final ResearchProfileService profileService;

    public ResearchProfileController(ResearchProfileService profileService) {
        super(profileService);
        this.profileService = profileService;
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_LEADER', 'SENIOR_RESEARCHER', 'RESEARCHER')")
    public ResponseEntity<BaseResponse<ResearchProfileDto>> getByUserId(@PathVariable Long userId) {
        ResearchProfileDto profile = profileService.getByUserId(userId);
        return success(profile, "Profile retrieved successfully");
    }
}
