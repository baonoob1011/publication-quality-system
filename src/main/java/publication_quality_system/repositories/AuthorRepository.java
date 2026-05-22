package publication_quality_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import publication_quality_system.entities.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}