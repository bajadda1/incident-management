package ma.bonmyd.backendincident.repositories.users;

import ma.bonmyd.backendincident.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String email);
}
