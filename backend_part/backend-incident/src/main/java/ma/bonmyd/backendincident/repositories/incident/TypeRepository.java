package ma.bonmyd.backendincident.repositories.incident;

import ma.bonmyd.backendincident.entities.incident.Sector;
import ma.bonmyd.backendincident.entities.incident.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type,Long> {
    Optional<Type>  findByName(String name);
    List<Type> findBySectorId(Long sectorId);
    // Retrieve the Sector associated with a given Type ID
    @Query("SELECT t.sector FROM Type t WHERE t.id = :typeId")
    Sector findSectorByTypeId(Long typeId);
}
