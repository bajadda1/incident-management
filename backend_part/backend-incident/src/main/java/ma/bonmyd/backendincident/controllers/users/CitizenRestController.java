package ma.bonmyd.backendincident.controllers.users;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import ma.bonmyd.backendincident.dtos.users.CitizenDTO;
import ma.bonmyd.backendincident.services.users.ICitizenService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("${citizen.api}")
public class CitizenRestController {

    private ICitizenService citizenService;

    @GetMapping("/{id}")
    public CitizenDTO getCitizenById(@Valid @PathVariable Long id) {
        return this.citizenService.getCitizenById(id);
    }

    @GetMapping("/imei/{imei}")
    public CitizenDTO getCitizenByImei(@PathVariable String imei) {
        return this.citizenService.getCitizenByIMEI(imei);
    }

    @GetMapping()
    public List<CitizenDTO> getCitizens() {
        return this.citizenService.getCitizens();
    }

    @PostMapping()
    public CitizenDTO createCitizen(@Valid @RequestBody CitizenDTO citizenDTO) {
        return this.citizenService.createCitizen(citizenDTO);
    }


    @DeleteMapping("/{id}")
    public String deleteCitizenById(@Valid @PathVariable Long id) {
        return this.citizenService.deleteCitizenById(id);
    }

    @DeleteMapping("/imei/{imei}")
    public String deleteCitizenByImei(@PathVariable String imei) {
        return this.citizenService.deleteCitizen(imei);
    }



}
