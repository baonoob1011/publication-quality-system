package publication_quality_system.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import publication_quality_system.base.BaseEntity;
import publication_quality_system.enums.SubmissionStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "papers")
@Getter
@Setter
public class Paper extends BaseEntity {

    @Column(name = "paper_code", nullable = false, unique = true)
    private String paperCode;

    @Column(nullable = false)
    private String title;

    @Column(name = "abstract_text", columnDefinition = "TEXT")
    private String abstractText;

    @Column(columnDefinition = "TEXT")
    private String keywords;

    @Column(name = "research_field")
    private String researchField;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "current_version")
    private Integer currentVersion = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_status")
    private SubmissionStatus submissionStatus = SubmissionStatus.DRAFT;

    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL)
    private List<PaperAuthor> authors = new ArrayList<>();

    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL)
    private List<PaperVersion> versions = new ArrayList<>();
}