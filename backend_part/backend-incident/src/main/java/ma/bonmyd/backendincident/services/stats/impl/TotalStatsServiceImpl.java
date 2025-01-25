package ma.bonmyd.backendincident.services.stats.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.bonmyd.backendincident.dtos.incident.IncidentStatusGroupDTO;
import ma.bonmyd.backendincident.dtos.stats.TotalStats;
import ma.bonmyd.backendincident.entities.incident.Incident;
import ma.bonmyd.backendincident.enums.Status;
import ma.bonmyd.backendincident.repositories.incident.IncidentRepository;
import ma.bonmyd.backendincident.repositories.incident.SectorRepository;
import ma.bonmyd.backendincident.repositories.users.CitizenRepository;
import ma.bonmyd.backendincident.repositories.users.UserRepository;
import ma.bonmyd.backendincident.specifications.IncidentSpecification;
import ma.bonmyd.backendincident.specifications.UserSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TotalStatsServiceImpl {
    private final IncidentRepository incidentRepository;
    private final CitizenRepository citizenRepository;
    private final SectorRepository sectorRepository;
    private final IncidentRepositoryCustomStatsImpl incidentRepositoryCustomStats;

    public TotalStats getTotalStats() {
        Long totalCitizens = this.citizenRepository.count();
        Long totalDeclaredIncidents = this.incidentRepository.count();
        Long totalProcessedIncidents = this.incidentRepository.count(IncidentSpecification.hasStatus(Status.PROCESSED));
        Long totalBlockedIncidents = this.incidentRepository.count(IncidentSpecification.hasStatus(Status.BLOCKED));
        Long totalSectors = this.sectorRepository.count();
        return TotalStats
                .builder()
                .totalCitizens(totalCitizens)
                .totalDeclaredIncidents(totalDeclaredIncidents)
                .totalProcessedIncidents(totalProcessedIncidents)
                .totalSectors(totalSectors)
                .build();
    }

    public List<IncidentStatusGroupDTO> getIncidentsGroupedByStatus() {
        return incidentRepository.findIncidentsGroupedByStatus();
    }

    public List<IncidentStatusGroupDTO> getGroupedByStatus(
            Long sectorId,
            Long regionId,
            Long typeId,
            Date startDate,
            Date endDate) {

        Specification<Incident> spec = Specification
                .where(IncidentSpecification.hasSectorId(sectorId))
                .and(IncidentSpecification.hasRegionId(regionId))
                .and(IncidentSpecification.hasTypeId(typeId))
                .and(IncidentSpecification.hasDateBetween(startDate, endDate));

        return incidentRepositoryCustomStats.findGroupedByStatus(spec);
    }
}
