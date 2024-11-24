package ma.bonmyd.backendincident.repositories.territoriale;

import ma.bonmyd.backendincident.entities.territoriale.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region,Long> {
}
