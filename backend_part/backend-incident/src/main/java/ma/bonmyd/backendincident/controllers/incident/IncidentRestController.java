package ma.bonmyd.backendincident.controllers.incident;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.bonmyd.backendincident.dtos.ApiResponseGenericPagination;
import ma.bonmyd.backendincident.dtos.incident.IncidentDTO;
import ma.bonmyd.backendincident.dtos.incident.IncidentDTOPagination;
import ma.bonmyd.backendincident.dtos.incident.IncidentUpdateDTO;
import ma.bonmyd.backendincident.dtos.incident.StatusDTO;
import ma.bonmyd.backendincident.dtos.users.UserResponseDTO;
import ma.bonmyd.backendincident.enums.Status;
import ma.bonmyd.backendincident.services.incident.IIncidentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("${incident.api}")
@RequiredArgsConstructor
public class IncidentRestController {
    private final IIncidentService incidentService;
    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @GetMapping
    public List<IncidentDTO> findAllIncidents() {
        return this.incidentService.findAllIncidents();
    }

    @GetMapping("/imei/{imei}")
    public List<IncidentDTO> findAllIncidentsByImei(@Valid @PathVariable String imei) {
        return this.incidentService.findAllIncidentsByImei(imei);
    }

    @GetMapping("/{id}")
    public IncidentDTO getIncident(@Valid @PathVariable Long id) {
        return this.incidentService.getIncident(id);
    }

    //    @Nullable @RequestParam(value = "photo",required = false)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public IncidentDTO createIncident(
            @Valid @RequestPart("file") MultipartFile photoFile,
            @Valid @RequestPart("incident") String incidentCreateDTOAsString
    ) throws JsonProcessingException {
        return this.incidentService.createIncident(incidentCreateDTOAsString, photoFile);
    }

    @PutMapping("/citizen-update")
    public IncidentDTO updateIncidentByCitizen(@RequestBody IncidentUpdateDTO incidentUpdateDTO) {
        return this.incidentService.updateIncidentByCitizen(incidentUpdateDTO);
    }

    @PutMapping("/admin-update/{incidentId}")
    public String updateIncidentByCitizen(@Valid @PathVariable Long incidentId,

                                          @RequestBody StatusDTO statusDTO) {
        return this.incidentService.updateIncidentStatus(incidentId, statusDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteIncident(@Valid @PathVariable Long id) {
        return this.incidentService.deleteIncident(id);
    }

    @GetMapping("/pagination")
    public ApiResponseGenericPagination<IncidentDTO> getProvincePages(@RequestParam(name = "current", defaultValue = "${default.current.page}") int current, @RequestParam(name = "size", defaultValue = "${default.page.size}") int size) {
        return this.incidentService.getIncidentsPage(current, size);
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

}
