package publication_quality_system.lab_member.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import publication_quality_system.base.BaseEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "research_group_members_details")
@SQLDelete(sql = "UPDATE research_group_members_details SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
public class ResearchGroupMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private ResearchGroup researchGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean isLeader = false;

    @Column(nullable = false)
    private boolean deleted = false;

    private String createdBy;
    private String updatedBy;
}
