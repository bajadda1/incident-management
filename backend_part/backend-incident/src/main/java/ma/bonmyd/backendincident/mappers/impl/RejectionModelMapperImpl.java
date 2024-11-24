package ma.bonmyd.backendincident.mappers.impl;

import ma.bonmyd.backendincident.dtos.incident.RejectionDTO;
import ma.bonmyd.backendincident.entities.incident.Rejection;
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
public class RejectionModelMapperImpl implements IModelMapper<Rejection, RejectionDTO> {

    private ModelMapper modelMapper;
    @Autowired
    public RejectionModelMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(Conditions.isNotNull());
    }

    @Override
    public RejectionDTO convertToDto(Rejection entity, Class<RejectionDTO> dtoClass) {
        if (entity == null) return null;

        return this.modelMapper.map(entity, dtoClass);
    }

    @Override
    public Rejection convertToEntity(RejectionDTO dto, Class<Rejection> entityClass) {
        if (dto == null) return null;
        return this.modelMapper.map(dto, entityClass);
    }

    @Override
    public List<RejectionDTO> convertListToListDto(Collection<Rejection> entityList, Class<RejectionDTO> outClass) {
        if (entityList == null) return Collections.emptyList();
        return entityList.stream()
                .map(entity -> convertToDto(entity, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public List<Rejection> convertListToListEntity(Collection<RejectionDTO> dtoList, Class<Rejection> outClass) {
        if (dtoList == null) return Collections.emptyList();
        return dtoList.stream()
                .map(dto -> convertToEntity(dto, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public Page<RejectionDTO> convertPageToPageDto(Page<Rejection> entityList, Class<RejectionDTO> outClass) {
        if (entityList == null) return Page.empty();
        return entityList.map(element -> modelMapper.map(element, outClass));
    }
}
