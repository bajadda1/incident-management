package ma.bonmyd.backendincident.repositories.incident;

import ma.bonmyd.backendincident.dtos.incident.IncidentStatusGroupDTO;
import ma.bonmyd.backendincident.entities.incident.Incident;
import ma.bonmyd.backendincident.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long>, JpaSpecificationExecutor<Incident> {
    List<Incident> findByCitizenImei(String imei);

    @Query("SELECT new ma.bonmyd.backendincident.dtos.incident.IncidentStatusGroupDTO(i.status, COUNT(i)) " +
            "FROM Incident i " +
            "GROUP BY i.status")
    List<IncidentStatusGroupDTO> findIncidentsGroupedByStatus();
}
