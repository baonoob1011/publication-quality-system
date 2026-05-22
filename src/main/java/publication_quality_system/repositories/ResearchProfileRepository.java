package publication_quality_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import publication_quality_system.entities.ResearchProfile;

import java.util.Optional;

@Repository
public interface ResearchProfileRepository extends JpaRepository<ResearchProfile, Long> {
    Optional<ResearchProfile> findByUserId(Long userId);
}
