package publication_quality_system.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import publication_quality_system.base.BaseEntity;

@Entity
@Table(name = "paper_versions")
@Getter
@Setter
public class PaperVersion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id", nullable = false)
    private Paper paper;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_type")
    private String fileType;
}