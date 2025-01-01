package ma.bonmyd.backendincident.mappers.impl;

import ma.bonmyd.backendincident.dtos.territoriale.RegionDTO;
import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTO;
import ma.bonmyd.backendincident.entities.territoriale.Region;
import ma.bonmyd.backendincident.entities.territoriale.Province;
import ma.bonmyd.backendincident.mappers.IModelMapper;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProvinceModelMapperImpl implements IModelMapper<Province, ProvinceDTO> {

    private ModelMapper modelMapper;
    private IModelMapper<Region,RegionDTO> regionModelMapper;
    @Autowired
    public ProvinceModelMapperImpl(ModelMapper modelMapper, IModelMapper<Region, RegionDTO> regionModelMapper) {
        this.modelMapper = modelMapper;
        this.regionModelMapper = regionModelMapper;
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(Conditions.isNotNull());
    }

    @Override
    public ProvinceDTO convertToDto(Province entity, Class<ProvinceDTO> dtoClass) {
        if (entity == null) return null;
        Region region=entity.getRegion();
        ProvinceDTO provinceDTO=this.modelMapper.map(entity, dtoClass);
        provinceDTO.setRegionDTO(this.regionModelMapper.convertToDto(region,RegionDTO.class));
        return provinceDTO;
    }

    @Override
    public Province convertToEntity(ProvinceDTO dto, Class<Province> entityClass) {
        if (dto == null) return null;
        RegionDTO regionDTO=dto.getRegionDTO();
        Province province=this.modelMapper.map(dto, entityClass);
        province.setRegion(this.regionModelMapper.convertToEntity(regionDTO,Region.class));
        return province;
    }

    @Override
    public List<ProvinceDTO> convertListToListDto(Collection<Province> entityList, Class<ProvinceDTO> outClass) {
        if (entityList == null) return Collections.emptyList();
        return entityList.stream()
                .map(entity -> convertToDto(entity, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public List<Province> convertListToListEntity(Collection<ProvinceDTO> dtoList, Class<Province> outClass) {
        if (dtoList == null) return Collections.emptyList();
        return dtoList.stream()
                .map(dto -> convertToEntity(dto, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProvinceDTO> convertPageToPageDto(Page<Province> entityList, Class<ProvinceDTO> outClass) {
        if (entityList == null) return Page.empty();
        return entityList.map(element -> modelMapper.map(element, outClass));
    }
}
