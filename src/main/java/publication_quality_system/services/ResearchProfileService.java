package publication_quality_system.services;

import publication_quality_system.base.BaseCrudService;
import publication_quality_system.dtos.ResearchProfileDto;

public interface ResearchProfileService extends BaseCrudService<ResearchProfileDto, Long> {
    ResearchProfileDto getByUserId(Long userId);
}
