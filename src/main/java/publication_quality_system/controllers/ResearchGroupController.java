package publication_quality_system.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import publication_quality_system.base.BaseCrudController;
import publication_quality_system.base.BaseResponse;
import publication_quality_system.dtos.ResearchGroupDto;
import publication_quality_system.dtos.ResearchGroupMemberDto;
import publication_quality_system.services.ResearchGroupService;

import java.util.List;

@RestController
@RequestMapping("/api/lab-members/research-groups")
public class ResearchGroupController extends BaseCrudController<ResearchGroupDto, Long> {

    private final ResearchGroupService groupService;

    public ResearchGroupController(ResearchGroupService groupService) {
        super(groupService);
        this.groupService = groupService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_READ')")
    public ResponseEntity<BaseResponse<List<ResearchGroupDto>>> getAll(Pageable pageable) {
        return success(groupService.getAll(pageable), "Research groups retrieved successfully");
    }

    @PostMapping("/{groupId}/members")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_MEMBER_MANAGE')")
    public ResponseEntity<BaseResponse<ResearchGroupMemberDto>> addMember(
            @PathVariable Long groupId,
            @Valid @RequestBody ResearchGroupMemberDto dto) {
        return created(groupService.addMember(groupId, dto));
    }

    @GetMapping("/{groupId}/members")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_READ')")
    public ResponseEntity<BaseResponse<List<ResearchGroupMemberDto>>> getMembers(@PathVariable Long groupId) {
        return success(groupService.getGroupMembers(groupId), "Research group members retrieved successfully");
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_MEMBER_MANAGE')")
    public ResponseEntity<BaseResponse<Void>> removeMember(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.removeMember(groupId, userId);
        return success(null, "Research group member removed successfully");
    }

    @PatchMapping("/{groupId}/members/{userId}/role")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_MEMBER_MANAGE')")
    public ResponseEntity<BaseResponse<ResearchGroupMemberDto>> changeMemberRole(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestBody ResearchGroupMemberDto dto) {
        return success(groupService.changeMemberRole(groupId, userId, dto), "Research group member role updated successfully");
    }

    @PatchMapping("/{groupId}/members/{userId}/status")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_MEMBER_MANAGE')")
    public ResponseEntity<BaseResponse<ResearchGroupMemberDto>> updateMemberStatus(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestBody ResearchGroupMemberDto dto) {
        return success(groupService.updateMemberStatus(groupId, userId, dto), "Research group member status updated successfully");
    }

    @PatchMapping("/{groupId}/leader/{userId}")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_MEMBER_MANAGE')")
    public ResponseEntity<BaseResponse<ResearchGroupMemberDto>> assignLeader(@PathVariable Long groupId, @PathVariable Long userId) {
        return success(groupService.assignGroupLeader(groupId, userId), "Research group leader assigned successfully");
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_READ')")
    public ResponseEntity<BaseResponse<List<ResearchGroupDto>>> getGroupsByUser(@PathVariable Long userId) {
        return success(groupService.getGroupsByUser(userId), "User research groups retrieved successfully");
    }
}
