package ma.bonmyd.backendincident.services.territoriale;

import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTOPagination;
import ma.bonmyd.backendincident.dtos.territoriale.RegionDTO;
import ma.bonmyd.backendincident.dtos.territoriale.RegionDTOPagination;
import ma.bonmyd.backendincident.entities.territoriale.Region;

import java.util.List;

public interface IRegionService {
    Region findRegionById(Long id);

    RegionDTO getRegion(Long id);

    List<RegionDTO> getRegions();

    RegionDTOPagination getRegionsPage(int currentPage, int size);
}
