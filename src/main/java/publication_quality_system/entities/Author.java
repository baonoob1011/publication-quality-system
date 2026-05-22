package publication_quality_system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import publication_quality_system.base.BaseEntity;

@Entity
@Table(name = "authors")
@Getter
@Setter
public class Author extends BaseEntity {

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    private String orcid;

    private String affiliation;
}