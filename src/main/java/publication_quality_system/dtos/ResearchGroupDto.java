package publication_quality_system.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResearchGroupDto {
    private Long id;

    @NotBlank(message = "Research group name cannot be empty")
    private String name;

    private String description;
    private String researchTopics;
    private String activeProjects;
    private String specialization;
    private String institution;
    private Integer totalPublications;
    private Integer acceptedPublications;
    private Double acceptanceRate;
    private Integer memberCount;
    private ResearchGroupMemberDto leader;
    private List<ResearchGroupMemberDto> members;
}
