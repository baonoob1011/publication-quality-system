package publication_quality_system.lab_member.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import publication_quality_system.base.BaseEntity;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "research_groups")
@SQLDelete(sql = "UPDATE research_groups SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
public class ResearchGroup extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    private String groupTopics;

    private String groupProjects;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private User leader;

    @Column(nullable = false)
    private boolean deleted = false;

    private String createdBy;
    private String updatedBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "research_group_members", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> members = new HashSet<>();
}
