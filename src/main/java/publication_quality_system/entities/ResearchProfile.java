package publication_quality_system.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import publication_quality_system.base.BaseEntity;
import publication_quality_system.enums.AcademicRank;
import publication_quality_system.enums.MemberStatus;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "research_profiles"
)
public class ResearchProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String avatarUrl;

    @Column(nullable = false, length = 255)
    private String institution;

    @Column(length = 255)
    private String affiliation;

    @Column(length = 255)
    private String department;

    @Column(length = 255)
    private String specialization;

    @Column(unique = true, length = 50)
    private String orcid;

    private String googleScholarUrl;

    private String researchGateUrl;

    private String scopusId;

    @Column(columnDefinition = "TEXT")
    private String researchInterests;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcademicRank academicRank = AcademicRank.RESEARCHER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    @Builder.Default
    private Integer totalPublications = 0;

    @Builder.Default
    private Integer totalReviews = 0;

    @Builder.Default
    private Integer acceptedPapers = 0;

    @Builder.Default
    private Integer citationCount = 0;

    @Builder.Default
    private Integer hIndex = 0;

    @Builder.Default
    private Double contributionScore = 0.0;
}