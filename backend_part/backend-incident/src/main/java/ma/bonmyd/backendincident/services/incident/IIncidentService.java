package ma.bonmyd.backendincident.services.incident;

import com.fasterxml.jackson.core.JsonProcessingException;
import ma.bonmyd.backendincident.dtos.ApiResponseGenericPagination;
import ma.bonmyd.backendincident.dtos.incident.*;
import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTOPagination;
import ma.bonmyd.backendincident.dtos.territoriale.RegionDTO;
import ma.bonmyd.backendincident.entities.incident.Incident;
import ma.bonmyd.backendincident.enums.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public interface IIncidentService {
    Incident findIncident(Long id);

    IncidentDTO getIncident(Long id);

    IncidentDTO createIncident(String incidentCreateDTOAsString, MultipartFile photoFile) throws JsonProcessingException;

    IncidentDTO updateIncidentByCitizen(IncidentUpdateDTO incidentUpdateDTO);

    IncidentDTO updateIncidentStatus(Long incidentId, StatusDTO statusDTO);

    IncidentDTO rejectIncident(Long incidentId, RejectionDTO rejectionDTO);

    String deleteIncident(Long id);

    ApiResponseGenericPagination<IncidentDTO> getFilteredIncidents(Status status,
                                                                   Long provinceId,
                                                                   Long regionId,
                                                                   Long sectorId,
                                                                   Long typeId,
                                                                   String description,
                                                                   Date date,
                                                                   int page, int size);

    List<IncidentDTO> getAllFilteredIncidents(Status status,
                                              Long provinceId,
                                              Long regionId,
                                              Long sectorId,
                                              Long typeId,
                                              String description,
                                              Date startDate,
                                              Date endDate);

    ApiResponseGenericPagination<IncidentDTO> getFilteredIncidentsByProfessional(Status status,
                                                                                 Long provinceId,
                                                                                 Long regionId,
                                                                                 Long sectorId,
                                                                                 Long typeId,
                                                                                 String description,
                                                                                 Date date,
                                                                                 int page, int size);


    ApiResponseGenericPagination<IncidentDTO> getFilteredIncidentsByCitizen(String imei,
                                                                            Status status,
                                                                            Long provinceId,
                                                                            Long regionId,
                                                                            Long sectorId,
                                                                            Long typeId,
                                                                            String description,
                                                                            Date date,
                                                                            int page, int size);


    List<IncidentStatusGroupDTO> getIncidentsGroupedByStatus();

}
