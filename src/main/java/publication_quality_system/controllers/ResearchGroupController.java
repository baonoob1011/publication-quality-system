package publication_quality_system.controllers;

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

    @PostMapping("/{groupId}/leader/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_LEADER')")
    public ResponseEntity<BaseResponse<Void>> assignLeader(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.assignLeader(groupId, userId);
        return success(null, "Leader assigned successfully");
    }

    @PostMapping("/{groupId}/members/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_LEADER', 'SENIOR_RESEARCHER')")
    public ResponseEntity<BaseResponse<Void>> addMember(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.addMember(groupId, userId);
        return success(null, "Member added successfully");
    }
}
