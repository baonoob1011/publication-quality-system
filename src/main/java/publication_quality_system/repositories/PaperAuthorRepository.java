package publication_quality_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import publication_quality_system.entities.PaperAuthor;

public interface PaperAuthorRepository extends JpaRepository<PaperAuthor, Long> {
}