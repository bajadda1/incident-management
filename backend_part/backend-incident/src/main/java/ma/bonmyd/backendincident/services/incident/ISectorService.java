package ma.bonmyd.backendincident.services.incident;

import ma.bonmyd.backendincident.dtos.incident.SectorDTO;
import ma.bonmyd.backendincident.entities.incident.Sector;

import java.util.List;

public interface ISectorService {

    Sector findSectorById(Long id);
    Sector findSectorByName(String name);
    SectorDTO getSector(Long id);
    List<SectorDTO> getSectors();
    SectorDTO createSector(SectorDTO sectorDTO);
    SectorDTO updateSector(SectorDTO sectorDTO);
    String deleteSector(Long id);
    SectorDTO getSectorByTypeId(Long typeId);

}
