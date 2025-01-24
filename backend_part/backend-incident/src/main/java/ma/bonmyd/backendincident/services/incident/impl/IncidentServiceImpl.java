package ma.bonmyd.backendincident.services.incident.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.bonmyd.backendincident.dtos.ApiResponseGenericPagination;
import ma.bonmyd.backendincident.dtos.incident.*;
import ma.bonmyd.backendincident.dtos.users.CitizenDTO;
import ma.bonmyd.backendincident.entities.incident.Incident;
import ma.bonmyd.backendincident.entities.incident.Rejection;
import ma.bonmyd.backendincident.entities.users.Citizen;
import ma.bonmyd.backendincident.enums.Status;
import ma.bonmyd.backendincident.exceptions.ResourceNotFoundException;
import ma.bonmyd.backendincident.mappers.IModelMapper;
import ma.bonmyd.backendincident.repositories.incident.IncidentRepository;
import ma.bonmyd.backendincident.repositories.incident.RejectionRepository;
import ma.bonmyd.backendincident.services.incident.IIncidentService;
import ma.bonmyd.backendincident.services.territoriale.IProvinceService;
import ma.bonmyd.backendincident.services.users.ICitizenService;
import ma.bonmyd.backendincident.specifications.IncidentSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class IncidentServiceImpl implements IIncidentService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final IncidentRepository incidentRepository;
    private final RejectionRepository rejectionRepository;
    private final IModelMapper<Incident, IncidentCreateDTO> incidentCreateModelMapper;
    private final IModelMapper<Incident, IncidentUpdateDTO> incidentUpdateModelMapper;
    private final ICitizenService citizenService;
    private final IModelMapper<Citizen, CitizenDTO> citizenModelMapper;
    private final IModelMapper<Incident, IncidentDTO> incidentModelMapper;
    private final IModelMapper<Rejection, RejectionDTO> rejectionModelMapper;
    private final IProvinceService provinceService;
    private final ObjectMapper objectMapper;


    @Override
    public Incident findIncident(Long id) {
        return this.incidentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("incident with id=%d not found", id)));
    }

    @Override
    public IncidentDTO getIncident(Long id) {
        Incident incident = this.findIncident(id);
        incident.setPhoto(getFileNameFromPath(incident.getPhoto()));
        return this.incidentModelMapper.convertToDto(incident, IncidentDTO.class);
    }


    @Override
    public IncidentDTO createIncident(String incidentCreateDTOAsString, MultipartFile photoFile) throws JsonProcessingException {

        IncidentCreateDTO incidentCreateDTO;
        //we have to use a simple variable beside MultipartFile in controller
        //=>we get the dto as a string and then map it to json from => "{k:v}"=>{k:v}
        incidentCreateDTO = this.objectMapper.readValue(incidentCreateDTOAsString, IncidentCreateDTO.class);
        //get the citizen imei
        String citizenIMEI = incidentCreateDTO.getCitizenIMEI();
        //find the citizen in db
        Citizen citizen = this.citizenService.findCitizenByIMEI(citizenIMEI);
        //if not exists => Create it

        if (citizen == null) {
            CitizenDTO citizenDTO = this.citizenService.createCitizen(CitizenDTO.builder().imei(citizenIMEI).build());
            citizen = this.citizenModelMapper.convertToEntity(citizenDTO, Citizen.class);
        }

        Incident incident = this.incidentCreateModelMapper.convertToEntity(incidentCreateDTO, Incident.class);
        //create calculated values
        incident.setCreatedAt(new Date());

        //where this incident is located (inside province layer)
        incident.setProvince(this.provinceService.findProvinceContainingPoint(incident.getLocation()));
        incident.setStatus(Status.DECLARED);

        //store it in file storage sys and get the photo uri to persist it in db
        String photoPath = uploadPhoto(photoFile);
        // Set the photo path in the Incident entity
        incident.setPhoto(photoPath);
        //set the citizen
        incident.setCitizen(citizen);
        this.incidentRepository.save(incident);
        return this.incidentModelMapper.convertToDto(incident, IncidentDTO.class);
    }

    @Override
    public IncidentDTO updateIncidentByCitizen(IncidentUpdateDTO incidentUpdateDTO) {
        Incident incidentToUpdate = this.findIncident(incidentUpdateDTO.getId());
        Incident incident = this.incidentUpdateModelMapper.convertToEntity(incidentUpdateDTO, Incident.class);
        incidentToUpdate.setType(incident.getType());
        incidentToUpdate.setSector(incident.getSector());
        incidentToUpdate.setUpdatedAt(new Date());
        incidentToUpdate.setDescription(incident.getDescription());
        incident.setStatus(Status.DECLARED);
        this.incidentRepository.save(incidentToUpdate);
        return this.incidentModelMapper.convertToDto(incidentToUpdate, IncidentDTO.class);
    }


    @Override
    public IncidentDTO updateIncidentStatus(Long incidentId, StatusDTO statusDTO) {

        Incident incidentToUpdate = this.findIncident(incidentId);

        if (canUpdateStatus(incidentToUpdate.getStatus(), statusDTO.getStatus())) {
            throw new RuntimeException("Invalid status transition");
        }

        incidentToUpdate.setStatus(statusDTO.getStatus());

        Incident updatedIncident = this.incidentRepository.save(incidentToUpdate);
        return this.incidentModelMapper.convertToDto(updatedIncident, IncidentDTO.class);
    }


    @Override
    public IncidentDTO rejectIncident(Long incidentId, RejectionDTO rejectionDTO) {

        Incident incidentToReject = this.findIncident(incidentId);

        if (canUpdateStatus(incidentToReject.getStatus(), Status.REJECTED)) {
            throw new RuntimeException("Invalid status transition");
        }

        incidentToReject.setStatus(Status.REJECTED);
        incidentToReject.setUpdatedAt(new Date());

        Rejection rejection = this.rejectionModelMapper.convertToEntity(rejectionDTO, Rejection.class);
        rejection.setIncident(incidentToReject);
        rejection.setDate(new Date());
        Incident incident = this.incidentRepository.save(incidentToReject);
        this.rejectionRepository.save(rejection);

        return this.incidentModelMapper.convertToDto(incident, IncidentDTO.class);
    }


    @Override
    public String deleteIncident(Long id) {
        this.incidentRepository.deleteById(id);
        return String.format("incident with id=%d has been deleted", id);
    }


    @Override
    public ApiResponseGenericPagination<IncidentDTO> getFilteredIncidents(Status status, Long provinceId, Long regionId, Long sectorId, Long typeId, String description, Date date, int page, int size) {

        Specification<Incident> incidentSpecification = Specification.where(IncidentSpecification.hasSectorId(sectorId)).and(IncidentSpecification.hasStatus(status)).and(IncidentSpecification.hasProvinceId(provinceId)).and(IncidentSpecification.hasRegionId(regionId)).and(IncidentSpecification.hasTypeId(typeId)).and(IncidentSpecification.descriptionContains(description)).and(IncidentSpecification.hasDate(date));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Incident> incidentPage = incidentRepository.findAll(incidentSpecification, pageable);

        // Map the Page of entities to a Page of DTOs
        Page<IncidentDTO> incidentDTOPage = incidentModelMapper.convertPageToPageDto(incidentPage, IncidentDTO.class);

        List<IncidentDTO> updatedList = incidentDTOPage.getContent().stream()
                .peek(incidentDTO -> {
                    // Create a copy or update the object
                    incidentDTO.setPhoto(getFileNameFromPath(incidentDTO.getPhoto()));
                })
                .toList();        // Build response
        return ApiResponseGenericPagination.<IncidentDTO>builder().currentPage(incidentDTOPage.getNumber()).pageSize(incidentDTOPage.getSize()).totalPages(incidentDTOPage.getTotalPages()).totalElements(incidentDTOPage.getTotalElements()).list(updatedList).build();
    }

    @Override
    public List<IncidentDTO> getAllFilteredIncidents(Status status, Long provinceId, Long regionId, Long sectorId, Long typeId, String description, Date startDate, Date endDate) {

        Specification<Incident> incidentSpecification = Specification
                .where(IncidentSpecification.hasSectorId(sectorId))
                .and(IncidentSpecification.hasStatus(status))
                .and(IncidentSpecification.hasProvinceId(provinceId))
                .and(IncidentSpecification.hasRegionId(regionId))
                .and(IncidentSpecification.hasTypeId(typeId))
                .and(IncidentSpecification.descriptionContains(description))
                .and(IncidentSpecification.hasDateBetween(startDate, endDate));

        List<Incident> incidents = incidentRepository.findAll(incidentSpecification);

        // Map the Page of entities to a Page of DTOs

        // Build response
        return incidentModelMapper.convertListToListDto(incidents, IncidentDTO.class);
    }

    @Override
    public ApiResponseGenericPagination<IncidentDTO> getFilteredIncidentsByProfessional(Status status, Long provinceId, Long regionId, Long sectorId, Long typeId, String description, Date date, int page, int size) {
        List<Status> allowedStatuses = List.of(Status.PUBLISHED, Status.PROCESSED, Status.IN_PROGRESS, Status.BLOCKED);
        Specification<Incident> incidentSpecification = Specification.where(IncidentSpecification.hasSectorId(sectorId)).and(IncidentSpecification.hasAllowedStatuses(allowedStatuses)).and(IncidentSpecification.hasStatus(status)).and(IncidentSpecification.hasProvinceId(provinceId)).and(IncidentSpecification.hasRegionId(regionId)).and(IncidentSpecification.hasTypeId(typeId)).and(IncidentSpecification.descriptionContains(description)).and(IncidentSpecification.hasDate(date));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Incident> incidentPage = incidentRepository.findAll(incidentSpecification, pageable);

        // Map the Page of entities to a Page of DTOs
        Page<IncidentDTO> incidentDTOPage = incidentModelMapper.convertPageToPageDto(incidentPage, IncidentDTO.class);

        // Build response
        return ApiResponseGenericPagination.<IncidentDTO>builder().currentPage(incidentDTOPage.getNumber()).pageSize(incidentDTOPage.getSize()).totalPages(incidentDTOPage.getTotalPages()).totalElements(incidentDTOPage.getTotalElements()).list(incidentDTOPage.getContent()).build();
    }

    @Override
    public ApiResponseGenericPagination<IncidentDTO> getFilteredIncidentsByCitizen(String imei, Status status, Long provinceId, Long regionId, Long sectorId, Long typeId, String description, Date date, int page, int size) {
        Specification<Incident> incidentSpecification = Specification.where(IncidentSpecification.hasCitizenImei(imei)).and(IncidentSpecification.hasSectorId(sectorId)).and(IncidentSpecification.hasStatus(status)).and(IncidentSpecification.hasProvinceId(provinceId)).and(IncidentSpecification.hasRegionId(regionId)).and(IncidentSpecification.hasTypeId(typeId)).and(IncidentSpecification.descriptionContains(description)).and(IncidentSpecification.hasDate(date));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Incident> incidentPage = incidentRepository.findAll(incidentSpecification, pageable);

        // Map the Page of entities to a Page of DTOs
        Page<IncidentDTO> incidentDTOPage = incidentModelMapper.convertPageToPageDto(incidentPage, IncidentDTO.class);

        // Build response
        return ApiResponseGenericPagination.<IncidentDTO>builder().currentPage(incidentDTOPage.getNumber()).pageSize(incidentDTOPage.getSize()).totalPages(incidentDTOPage.getTotalPages()).totalElements(incidentDTOPage.getTotalElements()).list(incidentDTOPage.getContent()).build();
    }

    @Override
    public List<IncidentStatusGroupDTO> getIncidentsGroupedByStatus() {
        return incidentRepository.findIncidentsGroupedByStatus();
    }

    public boolean canUpdateStatus(Status oldStatus, Status newStatus) {
        // Define allowed transitions
        Map<Status, List<Status>> allowedTransitions = Map.of(Status.DECLARED, List.of(Status.PUBLISHED, Status.REJECTED),
                // DECLARED -> PUBLISHED or REJECTED
                Status.REJECTED, List.of(), // No transitions allowed
                Status.PUBLISHED, List.of(Status.IN_PROGRESS), // PUBLISHED -> IN_PROGRESS
                Status.IN_PROGRESS, List.of(Status.PROCESSED, Status.BLOCKED), // IN_PROGRESS -> PROCESSED or BLOCKED
                Status.PROCESSED, List.of(), // Final state, no transitions allowed
                Status.BLOCKED, List.of() // Final state, no transitions allowed
        );
        // Check if the new status is allowed for the current old status
        List<Status> validNextStatuses = allowedTransitions.getOrDefault(oldStatus, List.of());
        return !validNextStatuses.contains(newStatus);
    }

    private String uploadPhoto(MultipartFile file) {
        // Check if the upload directory exists, create if it doesn't
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory", e);
            }
        }

        // Generate a unique file name for the image
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + "." + fileExtension;
        Path filePath = uploadPath.resolve(fileName);

        // Save the file to the file system
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Could not store the file", e);
        }

        // Return the file path or URL (adjust for your setup)
        return filePath.toString();
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }

    private String getFileNameFromPath(String filePath) {
        return Paths.get(filePath).getFileName().toString();
    }
}
