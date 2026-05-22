package publication_quality_system.lab_member.dtos;

import lombok.Data;
import publication_quality_system.lab_member.enums.AcademicRank;
import publication_quality_system.lab_member.enums.MemberStatus;

@Data
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
