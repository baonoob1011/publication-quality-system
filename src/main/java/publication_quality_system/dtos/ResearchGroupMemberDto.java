package publication_quality_system.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import publication_quality_system.enums.MemberRoleInGroup;
import publication_quality_system.enums.MemberStatus;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResearchGroupMemberDto {
    private Long id;

    @NotNull(message = "User id cannot be empty")
    private Long userId;
    private String fullName;
    private String email;
    private MemberRoleInGroup role;
    private MemberStatus status;
    private LocalDate joinedAt;
    private LocalDate leftAt;
    private Double contributionScore;
    private Integer assignedReviews;
    private Integer completedReviews;
    private String responsibilities;
}
