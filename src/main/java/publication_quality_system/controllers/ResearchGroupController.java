package publication_quality_system.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import publication_quality_system.base.BaseCrudController;
import publication_quality_system.base.BaseResponse;
import publication_quality_system.dtos.ResearchGroupDto;
import publication_quality_system.services.ResearchGroupService;

@RestController
@RequestMapping("/api/lab-members/research-groups")
public class ResearchGroupController extends BaseCrudController<ResearchGroupDto, Long> {

    private final ResearchGroupService groupService;

    public ResearchGroupController(ResearchGroupService groupService) {
        super(groupService);
        this.groupService = groupService;
    }

    @Override
    @PostMapping
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_CREATE')")
    public ResponseEntity<BaseResponse<ResearchGroupDto>> create(@Valid @RequestBody ResearchGroupDto dto) {
        return super.create(dto);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_READ')")
    public ResponseEntity<BaseResponse<ResearchGroupDto>> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_UPDATE')")
    public ResponseEntity<BaseResponse<ResearchGroupDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody ResearchGroupDto dto) {
        return super.update(id, dto);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_DELETE')")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        return super.delete(id);
    }

    @PostMapping("/{groupId}/leader/{userId}")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_UPDATE')")
    public ResponseEntity<BaseResponse<Void>> assignLeader(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.assignLeader(groupId, userId);
        return success(null, "Leader assigned successfully");
    }

    @PostMapping("/{groupId}/members/{userId}")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_UPDATE')")
    public ResponseEntity<BaseResponse<Void>> addMember(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.addMember(groupId, userId);
        return success(null, "Member added successfully");
    }
}
