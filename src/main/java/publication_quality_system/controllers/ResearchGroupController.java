package publication_quality_system.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import publication_quality_system.base.BaseController;
import publication_quality_system.base.BaseResponse;
import publication_quality_system.dtos.ResearchGroupDto;
import publication_quality_system.dtos.ResearchGroupMemberDto;
import publication_quality_system.services.ResearchGroupService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lab-members")
public class ResearchGroupController extends BaseController {

    private final ResearchGroupService groupService;

    @PostMapping("/research-groups")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_CREATE')")
    public ResponseEntity<BaseResponse<ResearchGroupDto>> create(@Valid @RequestBody ResearchGroupDto dto) {
        return created(groupService.createGroup(dto));
    }

    @GetMapping("/research-groups/{id}")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_READ')")
    public ResponseEntity<BaseResponse<ResearchGroupDto>> getById(@PathVariable Long id) {
        return success(groupService.getGroupById(id), "Research group retrieved successfully");
    }

    @GetMapping("/research-groups")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_READ')")
    public ResponseEntity<BaseResponse<List<ResearchGroupDto>>> getAll(Pageable pageable) {
        return success(groupService.getAllGroups(pageable), "Research groups retrieved successfully");
    }

    @PutMapping("/research-groups/{id}")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_UPDATE')")
    public ResponseEntity<BaseResponse<ResearchGroupDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody ResearchGroupDto dto) {
        return success(groupService.updateGroup(id, dto), "Research group updated successfully");
    }

    @DeleteMapping("/research-groups/{id}")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_DELETE')")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return success(null, "Research group deleted successfully");
    }

    @PostMapping("/research-groups/{groupId}/members")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_MEMBER_MANAGE')")
    public ResponseEntity<BaseResponse<ResearchGroupMemberDto>> addMember(
            @PathVariable Long groupId,
            @Valid @RequestBody ResearchGroupMemberDto dto) {
        return created(groupService.addMember(groupId, dto));
    }

    @GetMapping("/research-groups/{groupId}/members")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_READ')")
    public ResponseEntity<BaseResponse<List<ResearchGroupMemberDto>>> getMembers(@PathVariable Long groupId) {
        return success(groupService.getGroupMembers(groupId), "Research group members retrieved successfully");
    }

    @DeleteMapping("/research-groups/{groupId}/members/{userId}")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_MEMBER_MANAGE')")
    public ResponseEntity<BaseResponse<Void>> removeMember(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.removeMember(groupId, userId);
        return success(null, "Research group member removed successfully");
    }

    @PatchMapping("/research-groups/{groupId}/members/{userId}/role")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_MEMBER_MANAGE')")
    public ResponseEntity<BaseResponse<ResearchGroupMemberDto>> changeMemberRole(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestBody ResearchGroupMemberDto dto) {
        return success(groupService.changeMemberRole(groupId, userId, dto), "Research group member role updated successfully");
    }

    @PatchMapping("/research-groups/{groupId}/members/{userId}/status")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_MEMBER_MANAGE')")
    public ResponseEntity<BaseResponse<ResearchGroupMemberDto>> updateMemberStatus(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestBody ResearchGroupMemberDto dto) {
        return success(groupService.updateMemberStatus(groupId, userId, dto), "Research group member status updated successfully");
    }

    @PatchMapping("/research-groups/{groupId}/leader/{userId}")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_MEMBER_MANAGE')")
    public ResponseEntity<BaseResponse<ResearchGroupMemberDto>> assignLeader(@PathVariable Long groupId, @PathVariable Long userId) {
        return success(groupService.assignGroupLeader(groupId, userId), "Research group leader assigned successfully");
    }

    @GetMapping("/users/{userId}/research-groups")
    @PreAuthorize("hasAuthority('RESEARCH_GROUP_READ')")
    public ResponseEntity<BaseResponse<List<ResearchGroupDto>>> getGroupsByUser(@PathVariable Long userId) {
        return success(groupService.getGroupsByUser(userId), "User research groups retrieved successfully");
    }
}
