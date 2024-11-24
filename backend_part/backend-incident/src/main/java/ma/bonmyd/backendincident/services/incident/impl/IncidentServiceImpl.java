package ma.bonmyd.backendincident.services.incident.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import ma.bonmyd.backendincident.dtos.incident.*;
import ma.bonmyd.backendincident.dtos.territoriale.RegionDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class IncidentServiceImpl implements IIncidentService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private IncidentRepository incidentRepository;
    private RejectionRepository rejectionRepository;
    private IModelMapper<Incident, IncidentDTO> incidentModelMapper;
    private IModelMapper<Incident, IncidentCreateDTO> incidentCreateModelMapper;
    private IModelMapper<Incident, IncidentUpdateDTO> incidentUpdateModelMapper;
    private ICitizenService citizenService;
    private IModelMapper<Citizen,CitizenDTO> citizenModelMapper;
    private IModelMapper<Rejection,RejectionDTO> rejectionModelMapper;
    private IProvinceService provinceService;
    private ObjectMapper objectMapper;


    @Autowired
    public IncidentServiceImpl(IncidentRepository incidentRepository, RejectionRepository rejectionRepository, IModelMapper<Incident, IncidentDTO> incidentModelMapper, IModelMapper<Incident, IncidentCreateDTO> incidentCreateModelMapper, IModelMapper<Incident, IncidentUpdateDTO> incidentUpdateModelMapper, @Qualifier("citizenServiceImpl") ICitizenService citizenService, IModelMapper<Citizen, CitizenDTO> citizenModelMapper, IModelMapper<Rejection, RejectionDTO> rejectionModelMapper, @Qualifier("provinceServiceImpl") IProvinceService provinceService, ObjectMapper objectMapper) {
        this.incidentRepository = incidentRepository;
        this.rejectionRepository = rejectionRepository;
        this.incidentModelMapper = incidentModelMapper;
        this.incidentCreateModelMapper = incidentCreateModelMapper;
        this.incidentUpdateModelMapper = incidentUpdateModelMapper;
        this.citizenService = citizenService;
        this.citizenModelMapper = citizenModelMapper;
        this.rejectionModelMapper = rejectionModelMapper;
        this.provinceService = provinceService;
        this.objectMapper = objectMapper;
    }


    @Override
    public Incident findIncident(Long id) {
        return this.incidentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("incident with id=%d not found", id)));
    }

    @Override
    public IncidentDTO getIncident(Long id) {
        Incident incident = this.findIncident(id);
        return this.incidentModelMapper.convertToDto(incident, IncidentDTO.class);
    }

    @Override
    public List<IncidentDTO> findAllIncidents() {
        List<Incident> incidents = this.incidentRepository.findAll();
        return this.incidentModelMapper.convertListToListDto(incidents, IncidentDTO.class);
    }

    @Override
    public List<IncidentDTO> findAllIncidentsByImei(String imei) {
        List<Incident> incidents = this.incidentRepository.findByCitizenImei(imei);
        return this.incidentModelMapper.convertListToListDto(incidents, IncidentDTO.class);
    }

    @Override
    public IncidentDTO createIncident(String incidentCreateDTOAsString,MultipartFile photoFile) throws JsonProcessingException {

        IncidentCreateDTO incidentCreateDTO;
        //we have to use a simple variable beside MultipartFile in controller
        //=>we get the dto as a string and then map it to json from => "{k:v}"=>{k:v}
        incidentCreateDTO= this.objectMapper.readValue(incidentCreateDTOAsString,IncidentCreateDTO.class);
        //get the citizen imei
        String citizenIMEI = incidentCreateDTO.getCitizenIMEI();
        //find the citizen in db
        Citizen citizen = this.citizenService.findCitizenByIMEI(citizenIMEI);
        //if not exists => Create it

        if (citizen == null) {
            CitizenDTO citizenDTO=this.citizenService.createCitizen(CitizenDTO.builder().imei(citizenIMEI).build());
            citizen =this.citizenModelMapper.convertToEntity(citizenDTO,Citizen.class);
        }

        Incident incident = this.incidentCreateModelMapper.convertToEntity(incidentCreateDTO, Incident.class);
        //create calculated values
        incident.setCreatedAt(new Date());
        incident.setUpdatedAt(new Date());

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
        return this.incidentModelMapper.convertToDto(incident,IncidentDTO.class);
    }

    @Override
    public IncidentDTO updateIncidentByCitizen(IncidentUpdateDTO incidentUpdateDTO) {
        Incident incidentToUpdate=this.findIncident(incidentUpdateDTO.getId());
        Incident incident=this.incidentUpdateModelMapper.convertToEntity(incidentUpdateDTO,Incident.class);
        incidentToUpdate.setType(incident.getType());
        incidentToUpdate.setSector(incident.getSector());
        incidentToUpdate.setUpdatedAt(new Date());
        incidentToUpdate.setDescription(incident.getDescription());
        incident.setStatus(Status.DECLARED);
        this.incidentRepository.save(incidentToUpdate);
        return this.incidentModelMapper.convertToDto(incidentToUpdate,IncidentDTO.class);
    }

    @Override
    public String updateIncidentStatus(Long incidentId, Status status) {
        Incident incidentToUpdate=this.findIncident(incidentId);
        if(incidentToUpdate.getStatus()!=status){
            incidentToUpdate.setStatus(status);
            incidentToUpdate.setUpdatedAt(new Date());
        }
        this.incidentRepository.save(incidentToUpdate);
        return String.format("incident with id = %d status has been updated successfully",incidentId);
    }

    @Override
    public String rejectIncident(Long incidentId,  RejectionDTO rejectionDTO) {

        Incident incidentToReject=this.findIncident(incidentId);
        if (incidentToReject.getStatus()!=Status.PUBLISHED){
            incidentToReject.setStatus(Status.REJECTED);
            incidentToReject.setUpdatedAt(new Date());
        }

        Rejection rejection=this.rejectionModelMapper.convertToEntity(rejectionDTO,Rejection.class);
        rejection.setIncident(incidentToReject);

        this.incidentRepository.save(incidentToReject);
        this.rejectionRepository.save(rejection);

        return String.format("incident with id = %d  has been blocked",incidentId);
    }


    @Override
    public String deleteIncident(Long id) {
        this.incidentRepository.deleteById(id);
        return String.format("incident with id=%d has been deleted",id);
    }

    @Override
    public IncidentDTOPagination getIncidentsPage(int currentPage, int size) {
        Pageable pageable= PageRequest.of(currentPage,size);
        Page<Incident> incidents = this.incidentRepository.findAll(pageable);
        Page<IncidentDTO> incidentDTOS=this.incidentModelMapper.convertPageToPageDto(incidents,IncidentDTO.class);
        return IncidentDTOPagination
                .builder()
                .currentPage(currentPage)
                .pageSize(size)
                .incidentDTOS(incidentDTOS.getContent())
                .totalPages(incidentDTOS.getTotalPages())
                .build();
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

}
