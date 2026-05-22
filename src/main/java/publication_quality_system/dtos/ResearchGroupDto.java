package publication_quality_system.lab_member.dtos;

import lombok.Data;
import java.util.Set;

@Data
public class ResearchGroupDto {
    private Long id;
    private String name;
    private String description;
    private String groupTopics;
    private String groupProjects;
    private Long leaderId;
    private Set<Long> memberIds;
}
