package ma.bonmyd.backendincident.controllers.incident;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.bonmyd.backendincident.dtos.incident.IncidentDTO;
import ma.bonmyd.backendincident.dtos.incident.IncidentDTOPagination;
import ma.bonmyd.backendincident.dtos.incident.IncidentUpdateDTO;
import ma.bonmyd.backendincident.enums.Status;
import ma.bonmyd.backendincident.services.incident.IIncidentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${incident.api}")
@AllArgsConstructor
public class IncidentRestController {
    private IIncidentService incidentService;
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
            @Valid @RequestPart("incident") String incidentCreateDTOAsString) throws JsonProcessingException {
        return this.incidentService.createIncident(incidentCreateDTOAsString, photoFile);
    }

    @PutMapping ("/citizen-update")
    public IncidentDTO updateIncidentByCitizen( @RequestBody IncidentUpdateDTO incidentUpdateDTO){
        return this.incidentService.updateIncidentByCitizen(incidentUpdateDTO);
    }

    @PutMapping ("/admin-update/{incidentId}")
    public String updateIncidentByCitizen(@Valid @PathVariable Long incidentId,
                                          @RequestBody Status status){
        return this.incidentService.updateIncidentStatus(incidentId,status);
    }

    @DeleteMapping("/{id}")
    public String deleteIncident(@Valid @PathVariable Long id){
        return this.incidentService.deleteIncident(id);
    }

    @GetMapping("/pagination")
    public IncidentDTOPagination getProvincePages(@RequestParam(name = "current",defaultValue = "${default.current.page}") int current, @RequestParam(name = "size",defaultValue = "${default.page.size}") int size) {
        return this.incidentService.getIncidentsPage(current, size);
    }
}
