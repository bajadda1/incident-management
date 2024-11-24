package ma.bonmyd.backendincident.controllers.territoriale;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTO;
import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTOPagination;
import ma.bonmyd.backendincident.services.territoriale.IProvinceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("${province.api}")
public class ProvinceRestController {

    private IProvinceService provinceService;

    @GetMapping("/{id}")
    public ProvinceDTO getProvince(@Valid @PathVariable Long id) {
        return this.provinceService.getProvince(id);
    }

    @GetMapping()
    public List<ProvinceDTO> getProvinces() {
        return this.provinceService.getProvinces();
    }

    @GetMapping("/pagination")
    public ProvinceDTOPagination getProvincePages(@RequestParam(name = "current",defaultValue = "${default.current.page}") int current, @RequestParam(name = "size",defaultValue = "${default.page.size}") int size) {
        return this.provinceService.getProvincesPage(current, size);
    }
}
