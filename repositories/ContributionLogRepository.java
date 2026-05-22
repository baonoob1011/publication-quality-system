package publication_quality_system.lab_member.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import publication_quality_system.lab_member.entities.ContributionLog;

import java.util.List;

@Repository
public interface ContributionLogRepository extends JpaRepository<ContributionLog, Long> {
    Page<ContributionLog> findByUserId(Long userId, Pageable pageable);

    List<ContributionLog> findByUserId(Long userId);
}
