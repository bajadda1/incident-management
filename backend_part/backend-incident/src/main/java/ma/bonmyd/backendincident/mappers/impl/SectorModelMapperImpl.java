package ma.bonmyd.backendincident.mappers.impl;

import ma.bonmyd.backendincident.dtos.incident.SectorDTO;
import ma.bonmyd.backendincident.entities.incident.Sector;
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
public class SectorModelMapperImpl implements IModelMapper<Sector, SectorDTO> {

    private ModelMapper modelMapper;
    @Autowired
    public SectorModelMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(Conditions.isNotNull());
    }

    @Override
    public SectorDTO convertToDto(Sector entity, Class<SectorDTO> dtoClass) {
        if (entity == null) return null;

        return this.modelMapper.map(entity, dtoClass);
    }

    @Override
    public Sector convertToEntity(SectorDTO dto, Class<Sector> entityClass) {
        if (dto == null) return null;
        return this.modelMapper.map(dto, entityClass);
    }

    @Override
    public List<SectorDTO> convertListToListDto(Collection<Sector> entityList, Class<SectorDTO> outClass) {
        if (entityList == null) return Collections.emptyList();
        return entityList.stream()
                .map(entity -> convertToDto(entity, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public List<Sector> convertListToListEntity(Collection<SectorDTO> dtoList, Class<Sector> outClass) {
        if (dtoList == null) return Collections.emptyList();
        return dtoList.stream()
                .map(dto -> convertToEntity(dto, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public Page<SectorDTO> convertPageToPageDto(Page<Sector> entityList, Class<SectorDTO> outClass) {
        if (entityList == null) return Page.empty();
        return entityList.map(element -> modelMapper.map(element, outClass));
    }
}
