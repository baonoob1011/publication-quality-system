package publication_quality_system.lab_member.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import publication_quality_system.lab_member.entities.Role;
import publication_quality_system.lab_member.enums.SystemRole;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(SystemRole name);
}
