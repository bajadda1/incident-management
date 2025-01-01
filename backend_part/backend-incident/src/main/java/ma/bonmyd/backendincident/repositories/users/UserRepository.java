package ma.bonmyd.backendincident.repositories.users;

import ma.bonmyd.backendincident.entities.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String email);

    List<User> findByRoleRole(String role);

    @Query("SELECT u FROM User u WHERE LOWER(u.role.role) = LOWER(:role)")
    Page<User> findByRole(@Param("role") String role, Pageable pageable);
}
