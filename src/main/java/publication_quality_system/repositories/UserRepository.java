package publication_quality_system.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import publication_quality_system.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByFullNameContainingIgnoreCase(String fullName);

    List<User> findByRolesId(Long roleId);

    Page<User> findAllByDeletedFalse(Pageable pageable);
}
