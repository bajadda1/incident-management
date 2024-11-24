package ma.bonmyd.backendincident.repositories.users;

import ma.bonmyd.backendincident.entities.users.Role;
import ma.bonmyd.backendincident.entities.users.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {
    Optional<Token> findByToken(String token);
}
