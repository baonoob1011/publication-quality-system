package publication_quality_system.lab_member.services;

import publication_quality_system.base.BaseCrudService;
import publication_quality_system.lab_member.dtos.ResearchProfileDto;

public interface ResearchProfileService extends BaseCrudService<ResearchProfileDto, Long> {
    ResearchProfileDto getByUserId(Long userId);
}
