package ma.bonmyd.backendincident.controllers.territoriale;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTOPagination;
import ma.bonmyd.backendincident.dtos.territoriale.RegionDTO;
import ma.bonmyd.backendincident.dtos.territoriale.RegionDTOPagination;
import ma.bonmyd.backendincident.services.territoriale.IRegionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("${region.api}")
public class RegionRestController {

    private IRegionService regionService;

    @GetMapping("/{id}")
    public RegionDTO getRegion(@Valid @PathVariable Long id) {
        return this.regionService.getRegion(id);
    }

    @GetMapping()
    public List<RegionDTO> getRegions() {
        return this.regionService.getRegions();
    }

    @GetMapping("/pagination")
    public RegionDTOPagination getProvincePages(@RequestParam(name = "current",defaultValue = "${default.current.page}") int current, @RequestParam(name = "size",defaultValue = "${default.page.size}") int size) {
        return this.regionService.getRegionsPage(current, size);
    }
}
