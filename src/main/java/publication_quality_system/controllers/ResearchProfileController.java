package publication_quality_system.controllers;

import jakarta.validation.Valid;
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

    @Override
    @PostMapping
    @PreAuthorize("hasAuthority('RESEARCH_PROFILE_CREATE')")
    public ResponseEntity<BaseResponse<ResearchProfileDto>> create(@Valid @RequestBody ResearchProfileDto dto) {
        return super.create(dto);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('RESEARCH_PROFILE_READ')")
    public ResponseEntity<BaseResponse<ResearchProfileDto>> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('RESEARCH_PROFILE_UPDATE')")
    public ResponseEntity<BaseResponse<ResearchProfileDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody ResearchProfileDto dto) {
        return super.update(id, dto);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('RESEARCH_PROFILE_DELETE')")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        return super.delete(id);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('RESEARCH_PROFILE_READ')")
    public ResponseEntity<BaseResponse<ResearchProfileDto>> getByUserId(@PathVariable Long userId) {
        ResearchProfileDto profile = profileService.getByUserId(userId);
        return success(profile, "Profile retrieved successfully");
    }
}
