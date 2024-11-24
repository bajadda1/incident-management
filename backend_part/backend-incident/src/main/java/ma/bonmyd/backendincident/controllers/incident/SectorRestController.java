package ma.bonmyd.backendincident.controllers.incident;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.bonmyd.backendincident.dtos.incident.SectorDTO;
import ma.bonmyd.backendincident.services.incident.ISectorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${sector.api}")
@AllArgsConstructor
public class SectorRestController {

    private  ISectorService sectorService;

    // Get a sector by ID
    @GetMapping("/{id}")
    public SectorDTO getSectorById(@Valid  @PathVariable Long id) {
        return sectorService.getSector(id);
    }

    // Get all sectors
    @GetMapping
    public List<SectorDTO> getAllSectors() {
        return sectorService.getSectors();
    }

    // Create a new sector
    @PostMapping
    public SectorDTO createSector(@Valid @RequestBody SectorDTO sectorDTO) {
        return sectorService.createSector(sectorDTO);
    }

    // Update an existing sector
    @PutMapping()
    public SectorDTO updateSector(@RequestBody SectorDTO sectorDTO) {

        return sectorService.updateSector(sectorDTO);
    }

    // Delete a sector by ID
    @DeleteMapping("/{id}")
    public String deleteSector(@Valid @PathVariable Long id) {
        return sectorService.deleteSector(id);
    }

    // Get a sector that contains the type with typeId
    @GetMapping("/type-id/{typeId}")
    public SectorDTO getSectorByTypeId(@Valid @PathVariable Long typeId) {
        return sectorService.getSectorByTypeId(typeId);
    }
}

