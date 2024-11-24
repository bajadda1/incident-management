package ma.bonmyd.backendincident.repositories.incident;

import ma.bonmyd.backendincident.entities.incident.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectorRepository extends JpaRepository<Sector,Long> {
    Optional<Sector> findByName(String name);
}
