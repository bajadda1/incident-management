package ma.bonmyd.backendincident.mappers.impl;

import ma.bonmyd.backendincident.dtos.territoriale.RegionDTO;
import ma.bonmyd.backendincident.entities.territoriale.Region;
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
public class RegionModelMapperImpl implements IModelMapper<Region, RegionDTO> {

    private ModelMapper modelMapper;
    @Autowired
    public RegionModelMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(Conditions.isNotNull());
    }

    @Override
    public RegionDTO convertToDto(Region entity, Class<RegionDTO> dtoClass) {
        if (entity == null) return null;

        return this.modelMapper.map(entity, dtoClass);
    }

    @Override
    public Region convertToEntity(RegionDTO dto, Class<Region> entityClass) {
        if (dto == null) return null;
        return this.modelMapper.map(dto, entityClass);
    }

    @Override
    public List<RegionDTO> convertListToListDto(Collection<Region> entityList, Class<RegionDTO> outClass) {
        if (entityList == null) return Collections.emptyList();
        return entityList.stream()
                .map(entity -> convertToDto(entity, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public List<Region> convertListToListEntity(Collection<RegionDTO> dtoList, Class<Region> outClass) {
        if (dtoList == null) return Collections.emptyList();
        return dtoList.stream()
                .map(dto -> convertToEntity(dto, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public Page<RegionDTO> convertPageToPageDto(Page<Region> entityList, Class<RegionDTO> outClass) {
        if (entityList == null) return Page.empty();
        return entityList.map(element -> modelMapper.map(element, outClass));
    }
}
