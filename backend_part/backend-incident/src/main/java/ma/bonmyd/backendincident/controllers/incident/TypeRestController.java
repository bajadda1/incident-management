package ma.bonmyd.backendincident.controllers.incident;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.bonmyd.backendincident.dtos.incident.TypeDTO;
import ma.bonmyd.backendincident.services.incident.ITypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${type.api}")
@AllArgsConstructor
public class TypeRestController {

    private ITypeService typeService;

    // Get a type by ID
    @GetMapping("/{id}")
    public TypeDTO getTypeById(@Valid  @PathVariable Long id) {
        return typeService.getType(id);
    }

    // Get all types
    @GetMapping
    public List<TypeDTO> getTypes() {
        return typeService.getTypes();
    }

    // Create a new type
    @PostMapping("/{sectorId}")
    public TypeDTO createType(@Valid @PathVariable Long sectorId,@Valid @RequestBody TypeDTO typeDTO) {
        return typeService.createType(sectorId,typeDTO);
    }

    // Update an existing type
    @PutMapping("/{sectorId}")
    public TypeDTO updateType(@Valid @PathVariable Long sectorId,@Valid @RequestBody TypeDTO typeDTO) {
        return typeService.updateType(sectorId,typeDTO);
    }

    // Delete a type by ID
    @DeleteMapping("/{id}")
    public String deleteType(@Valid @PathVariable Long id) {
        return typeService.deleteType(id);
    }

    // Get all types belogging to a sector
    @GetMapping("sector-id/{sectorId}")
    public List<TypeDTO> getTypesBySectorId(@Valid @PathVariable Long sectorId) {
        return typeService.getTypesBySectorId(sectorId);
    }
}

