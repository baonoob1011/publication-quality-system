package publication_quality_system.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResearchGroupDto {
    private Long id;
    private String name;
    private String description;
    private String groupTopics;
    private String groupProjects;
    private Long leaderId;
    private Set<Long> memberIds;
}
