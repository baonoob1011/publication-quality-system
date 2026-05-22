package publication_quality_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import publication_quality_system.entities.ResearchGroup;
import publication_quality_system.entities.ResearchGroupMember;
import publication_quality_system.entities.User;

import java.util.Optional;

@Repository
public interface ResearchGroupMemberRepository extends JpaRepository<ResearchGroupMember, Long> {
    Optional<ResearchGroupMember> findByResearchGroupAndUser(ResearchGroup researchGroup, User user);
}
