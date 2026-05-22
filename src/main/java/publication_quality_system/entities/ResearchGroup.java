package publication_quality_system.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import publication_quality_system.base.BaseEntity;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "research_groups"
)

public class ResearchGroup extends BaseEntity {

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String researchTopics;

    @Column(columnDefinition = "TEXT")
    private String activeProjects;

    @Column(length = 255)
    private String specialization;

    @Column(length = 255)
    private String institution;

    @Builder.Default
    private Integer totalPublications = 0;

    @Builder.Default
    private Integer acceptedPublications = 0;

    @Builder.Default
    private Double acceptanceRate = 0.0;

    @OneToMany(
            mappedBy = "researchGroup",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private Set<ResearchGroupMember> memberships = new HashSet<>();
}