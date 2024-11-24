package ma.bonmyd.backendincident.services.territoriale.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTO;
import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTOPagination;
import ma.bonmyd.backendincident.dtos.territoriale.RegionDTO;
import ma.bonmyd.backendincident.dtos.territoriale.RegionDTOPagination;
import ma.bonmyd.backendincident.entities.territoriale.Province;
import ma.bonmyd.backendincident.entities.territoriale.Region;
import ma.bonmyd.backendincident.exceptions.ResourceNotFoundException;
import ma.bonmyd.backendincident.mappers.IModelMapper;
import ma.bonmyd.backendincident.repositories.territoriale.RegionRepository;
import ma.bonmyd.backendincident.services.territoriale.IRegionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class RegionServiceImpl implements IRegionService {

    private RegionRepository regionRepository;
    private IModelMapper<Region, RegionDTO> regionModelMapper;


    @Override
    public Region findRegionById(Long id) {
        return this.regionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("region with id=%d not found", id)));
    }

    @Override
    public RegionDTO getRegion(Long id) {
        Region region = this.findRegionById(id);
        return this.regionModelMapper.convertToDto(region, RegionDTO.class);
    }

    @Override
    public List<RegionDTO> getRegions() {
        List<Region> regions = this.regionRepository.findAll();
        return this.regionModelMapper.convertListToListDto(regions, RegionDTO.class);
    }

    @Override
    public RegionDTOPagination getRegionsPage(int currentPage, int size) {
        Pageable pageable = PageRequest.of(currentPage, size);
        Page<Region> regions = this.regionRepository.findAll(pageable);
        Page<RegionDTO> regionDTOS = this.regionModelMapper.convertPageToPageDto(regions, RegionDTO.class);
        return RegionDTOPagination.
                builder().
                currentPage(currentPage).
                pageSize(size).
                totalPages(regionDTOS.getTotalPages()).
                regionDTOS(regionDTOS.getContent()).
                build();
    }
}
