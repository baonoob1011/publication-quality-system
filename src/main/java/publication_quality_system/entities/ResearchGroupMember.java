package publication_quality_system.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import publication_quality_system.base.BaseEntity;
import publication_quality_system.enums.MemberRoleInGroup;
import publication_quality_system.enums.MemberStatus;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "research_group_members"
)

public class ResearchGroupMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private ResearchGroup researchGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRoleInGroup role = MemberRoleInGroup.MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    private LocalDate joinedAt;

    private LocalDate leftAt;

    @Builder.Default
    private Double contributionScore = 0.0;

    @Builder.Default
    private Integer assignedReviews = 0;

    @Builder.Default
    private Integer completedReviews = 0;

    @Column(columnDefinition = "TEXT")
    private String responsibilities;
}