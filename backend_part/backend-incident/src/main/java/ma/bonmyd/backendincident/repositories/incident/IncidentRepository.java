package ma.bonmyd.backendincident.repositories.incident;

import ma.bonmyd.backendincident.entities.incident.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident,Long> {
    List<Incident> findByCitizenImei(String imei);
}
