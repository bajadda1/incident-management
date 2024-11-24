package ma.bonmyd.backendincident.repositories.users;

import ma.bonmyd.backendincident.entities.users.Role;
import ma.bonmyd.backendincident.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRole(String role);
}
