package ma.bonmyd.backendincident.services.incident;

import com.fasterxml.jackson.core.JsonProcessingException;
import ma.bonmyd.backendincident.dtos.ApiResponseGenericPagination;
import ma.bonmyd.backendincident.dtos.incident.*;
import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTOPagination;
import ma.bonmyd.backendincident.dtos.territoriale.RegionDTO;
import ma.bonmyd.backendincident.entities.incident.Incident;
import ma.bonmyd.backendincident.enums.Status;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IIncidentService {
    Incident findIncident(Long id);
    IncidentDTO getIncident(Long id);
    List<IncidentDTO> findAllIncidents();
    List<IncidentDTO> findAllIncidentsByImei(String imei);
    IncidentDTO createIncident(String incidentCreateDTOAsString, MultipartFile photoFile) throws JsonProcessingException;
    IncidentDTO updateIncidentByCitizen(IncidentUpdateDTO incidentUpdateDTO);
    String updateIncidentStatus(Long incidentId, StatusDTO statusDTO);
    String rejectIncident(Long incidentId, RejectionDTO rejectionDTO);
    String deleteIncident(Long id);

    ApiResponseGenericPagination<IncidentDTO> getIncidentsPage(int currentPage, int size);


}
