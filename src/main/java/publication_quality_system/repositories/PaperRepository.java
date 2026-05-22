package publication_quality_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import publication_quality_system.entities.Paper;

public interface PaperRepository extends JpaRepository<Paper, Long> {
    boolean existsByPaperCode(String paperCode);
}
