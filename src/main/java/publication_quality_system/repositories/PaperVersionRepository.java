package publication_quality_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import publication_quality_system.entities.PaperAuthor;
import publication_quality_system.entities.PaperVersion;

public interface PaperVersionRepository extends JpaRepository<PaperVersion, Long> {
}