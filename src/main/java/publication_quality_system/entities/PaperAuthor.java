package publication_quality_system.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import publication_quality_system.base.BaseEntity;
import publication_quality_system.enums.AuthorRole;

@Entity
@Table(name = "paper_authors")
@Getter
@Setter
public class PaperAuthor extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id", nullable = false)
    private Paper paper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private AuthorRole role;
}