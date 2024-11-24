package ma.bonmyd.backendincident.repositories.incident;

import ma.bonmyd.backendincident.entities.incident.Rejection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RejectionRepository extends JpaRepository<Rejection,Long> {
}
