package publication_quality_system.lab_member.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import publication_quality_system.lab_member.entities.ResearchGroup;

@Repository
public interface ResearchGroupRepository extends JpaRepository<ResearchGroup, Long> {
    Page<ResearchGroup> findAllByDeletedFalse(Pageable pageable);
}
