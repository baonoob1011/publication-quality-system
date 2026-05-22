package publication_quality_system.services;

import org.springframework.data.domain.Pageable;
import publication_quality_system.base.BaseCrudService;
import publication_quality_system.dtos.ResearchGroupDto;
import publication_quality_system.dtos.ResearchGroupMemberDto;
import publication_quality_system.dtos.ResearchProfileDto;

import java.util.List;

public interface ResearchGroupService extends BaseCrudService<ResearchGroupDto, Long> {

    List<ResearchGroupDto> getAll(Pageable pageable);

    ResearchGroupMemberDto addMember(Long groupId, ResearchGroupMemberDto dto);

    void removeMember(Long groupId, Long userId);

    ResearchGroupMemberDto changeMemberRole(Long groupId, Long userId, ResearchGroupMemberDto dto);

    ResearchGroupMemberDto updateMemberStatus(Long groupId, Long userId, ResearchGroupMemberDto dto);

    ResearchGroupMemberDto assignGroupLeader(Long groupId, Long userId);

    List<ResearchGroupMemberDto> getGroupMembers(Long groupId);

    List<ResearchGroupDto> getGroupsByUser(Long userId);
}
