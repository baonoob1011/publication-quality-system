package publication_quality_system.services;

import org.springframework.data.domain.Pageable;
import publication_quality_system.dtos.ResearchGroupDto;
import publication_quality_system.dtos.ResearchGroupMemberDto;

import java.util.List;

public interface ResearchGroupService {
    ResearchGroupDto createGroup(ResearchGroupDto dto);

    ResearchGroupDto updateGroup(Long groupId, ResearchGroupDto dto);

    ResearchGroupDto getGroupById(Long groupId);

    List<ResearchGroupDto> getAllGroups(Pageable pageable);

    void deleteGroup(Long groupId);

    ResearchGroupMemberDto addMember(Long groupId, ResearchGroupMemberDto dto);

    void removeMember(Long groupId, Long userId);

    ResearchGroupMemberDto changeMemberRole(Long groupId, Long userId, ResearchGroupMemberDto dto);

    ResearchGroupMemberDto updateMemberStatus(Long groupId, Long userId, ResearchGroupMemberDto dto);

    ResearchGroupMemberDto assignGroupLeader(Long groupId, Long userId);

    List<ResearchGroupMemberDto> getGroupMembers(Long groupId);

    List<ResearchGroupDto> getGroupsByUser(Long userId);
}
