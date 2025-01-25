package ma.bonmyd.backendincident.controllers.incident;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.bonmyd.backendincident.dtos.ApiResponseGenericPagination;
import ma.bonmyd.backendincident.dtos.incident.*;
import ma.bonmyd.backendincident.dtos.users.UserResponseDTO;
import ma.bonmyd.backendincident.enums.Status;
import ma.bonmyd.backendincident.services.incident.IIncidentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("${incident.api}")
@RequiredArgsConstructor
public class IncidentRestController {
    private final IIncidentService incidentService;
    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;


    @GetMapping("/{id}")
    public IncidentDTO getIncident(@Valid @PathVariable Long id) {
        return this.incidentService.getIncident(id);
    }

    //    @Nullable @RequestParam(value = "photo",required = false)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            summary = "Create an Incident with file upload",
            description = "Allows users to create an incident by uploading a file and providing incident details.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Incident created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = IncidentDTO.class))
                    )
            }
    )
    public IncidentDTO createIncident(
            @Valid @RequestPart("file") MultipartFile photoFile,
            @Valid @RequestPart("incident") String incidentCreateDTOAsString
    ) throws JsonProcessingException {
        return this.incidentService.createIncident(incidentCreateDTOAsString, photoFile);
    }

    @PutMapping("/update/citizen")
    public IncidentDTO updateIncidentByCitizen(@RequestBody IncidentUpdateDTO incidentUpdateDTO) {
        return this.incidentService.updateIncidentByCitizen(incidentUpdateDTO);
    }

    @PutMapping("/update/{incidentId}")
    public IncidentDTO updateIncidentByCitizen(@Valid @PathVariable Long incidentId,
                                               @RequestBody StatusDTO statusDTO) {
        return this.incidentService.updateIncidentStatus(incidentId, statusDTO);
    }

    @PostMapping("/reject/{incidentId}")
    public IncidentDTO updateIncidentByCitizen(@Valid @PathVariable Long incidentId,
                                               @RequestBody RejectionDTO rejectionDTO) {
        return this.incidentService.rejectIncident(incidentId, rejectionDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteIncident(@Valid @PathVariable Long id) {
        return this.incidentService.deleteIncident(id);
    }


    @GetMapping("/photos/{filename}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Determine content type (e.g., image/png, image/jpeg)
            String contentType = MediaType.IMAGE_JPEG_VALUE; // Default to JPEG
            if (filename.endsWith(".png")) {
                contentType = MediaType.IMAGE_PNG_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search/admin")
    public ApiResponseGenericPagination<IncidentDTO> searchIncidents(
            @RequestParam(name = "status", required = false) Status status,
            @RequestParam(name = "province", required = false) Long provinceId,
            @RequestParam(name = "region", required = false) Long regionId,
            @RequestParam(name = "sector", required = false) Long sectorId,
            @RequestParam(name = "type", required = false) Long typeId,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "date", required = false) Date date,
            @RequestParam(name = "page", defaultValue = "${default.current.page}") int page,
            @RequestParam(name = "size", defaultValue = "${default.page.size}") int size) {
        return incidentService.getFilteredIncidents(status, provinceId, regionId, sectorId, typeId, description, date, page, size);
    }

    @GetMapping("/search/professional")
    public ApiResponseGenericPagination<IncidentDTO> searchIncidentsByProfessional(
            @RequestParam(name = "status", required = false) Status status,
            @RequestParam(name = "province", required = false) Long provinceId,
            @RequestParam(name = "region", required = false) Long regionId,
            @RequestParam(name = "sector", required = true) Long sectorId,
            @RequestParam(name = "type", required = false) Long typeId,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "date", required = false) Date date,
            @RequestParam(name = "page", defaultValue = "${default.current.page}") int page,
            @RequestParam(name = "size", defaultValue = "${default.page.size}") int size) {
        return incidentService.getFilteredIncidentsByProfessional(status, provinceId, regionId, sectorId, typeId, description, date, page, size);
    }

    @GetMapping("/search/citizen")
    public ApiResponseGenericPagination<IncidentDTO> searchIncidentsByCitizen(
            @RequestParam(name = "imei", required = true) String imei,
            @RequestParam(name = "status", required = false) Status status,
            @RequestParam(name = "province", required = false) Long provinceId,
            @RequestParam(name = "region", required = false) Long regionId,
            @RequestParam(name = "sector", required = false) Long sectorId,
            @RequestParam(name = "type", required = false) Long typeId,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "date", required = false) Date date,
            @RequestParam(name = "page", defaultValue = "${default.current.page}") int page,
            @RequestParam(name = "size", defaultValue = "${default.page.size}") int size) {
        return incidentService.getFilteredIncidentsByCitizen(imei, status, provinceId, regionId, sectorId, typeId, description, date, page, size);
    }


    @GetMapping()
    public List<IncidentDTO> getAllIncidents(
            @RequestParam(name = "status", required = false) Status status,
            @RequestParam(name = "province", required = false) Long provinceId,
            @RequestParam(name = "region", required = false) Long regionId,
            @RequestParam(name = "sector", required = false) Long sectorId,
            @RequestParam(name = "type", required = false) Long typeId,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "start-date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(name = "end-date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        return incidentService.getAllFilteredIncidents(status, provinceId, regionId, sectorId, typeId, description, startDate, endDate);
    }
}
