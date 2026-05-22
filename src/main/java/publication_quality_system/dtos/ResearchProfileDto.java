package publication_quality_system.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import publication_quality_system.enums.AcademicRank;
import publication_quality_system.enums.MemberStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResearchProfileDto {
    private Long id;
    private Long userId;
    private String avatarUrl;
    private String institution;
    private String specialization;
    private String orcid;
    private String researchInterests;
    private AcademicRank academicRank;
    private MemberStatus status;
}
