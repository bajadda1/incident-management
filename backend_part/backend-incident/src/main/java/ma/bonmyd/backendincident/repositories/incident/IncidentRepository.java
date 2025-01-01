package ma.bonmyd.backendincident.repositories.incident;

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

    @Query("SELECT i from Incident  i where i.sector.id=:sectorId and i.status in :statuses")
    List<Incident> findBySectorNameAndStatuses(@Param("sectorId") Long sectorId, @Param("statuses") List<Status> statuses);

    @Query("SELECT i from Incident i where i.sector.id=:sectorId and i.status in :statuses")
    Page<Incident> findBySectorNameAndStatuses(@Param("sectorId") Long sectorId, @Param("statuses") List<Status> statuses, Pageable pageable);


    Page<Incident> findByStatus(Status status, Pageable pageable);
}
