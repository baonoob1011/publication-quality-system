package publication_quality_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import publication_quality_system.entities.ResearchGroup;
import publication_quality_system.entities.ResearchGroupMember;
import publication_quality_system.entities.User;
import publication_quality_system.enums.MemberRoleInGroup;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResearchGroupMemberRepository extends JpaRepository<ResearchGroupMember, Long> {
    Optional<ResearchGroupMember> findByResearchGroupAndUser(ResearchGroup researchGroup, User user);

    boolean existsByResearchGroupIdAndUserId(Long groupId, Long userId);

    Optional<ResearchGroupMember> findByResearchGroupIdAndUserId(Long groupId, Long userId);

    List<ResearchGroupMember> findByResearchGroupId(Long groupId);

    List<ResearchGroupMember> findByUserId(Long userId);

    Optional<ResearchGroupMember> findByResearchGroupIdAndRole(Long groupId, MemberRoleInGroup role);
}
