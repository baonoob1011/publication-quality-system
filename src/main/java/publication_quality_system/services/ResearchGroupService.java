package publication_quality_system.lab_member.services;

import publication_quality_system.base.BaseCrudService;
import publication_quality_system.lab_member.dtos.ResearchGroupDto;

public interface ResearchGroupService extends BaseCrudService<ResearchGroupDto, Long> {
    void assignLeader(Long groupId, Long userId);

    void addMember(Long groupId, Long userId);
}
