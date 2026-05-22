package publication_quality_system.lab_member.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import publication_quality_system.base.BaseEntity;
import publication_quality_system.lab_member.enums.AcademicRank;
import publication_quality_system.lab_member.enums.MemberStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "research_profiles")
@SQLDelete(sql = "UPDATE research_profiles SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
public class ResearchProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String avatarUrl;

    private String institution;

    private String specialization;

    @Column(unique = true)
    private String orcid;

    private String researchInterests;

    @Enumerated(EnumType.STRING)
    private AcademicRank academicRank;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    @Column(nullable = false)
    private boolean deleted = false;

    private String createdBy;
    private String updatedBy;
}
