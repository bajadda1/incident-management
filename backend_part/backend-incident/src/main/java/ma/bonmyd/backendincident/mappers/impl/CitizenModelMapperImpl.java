package ma.bonmyd.backendincident.mappers.impl;


import ma.bonmyd.backendincident.dtos.users.CitizenDTO;
import ma.bonmyd.backendincident.entities.users.Citizen;
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
public class CitizenModelMapperImpl implements IModelMapper<Citizen, CitizenDTO> {

    private ModelMapper modelMapper;
    @Autowired
    public CitizenModelMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(Conditions.isNotNull());
    }

    @Override
    public CitizenDTO convertToDto(Citizen entity, Class<CitizenDTO> dtoClass) {
        if (entity == null) return null;

        return this.modelMapper.map(entity, dtoClass);
    }

    @Override
    public Citizen convertToEntity(CitizenDTO dto, Class<Citizen> entityClass) {
        if (dto == null) return null;
        return this.modelMapper.map(dto, entityClass);
    }

    @Override
    public List<CitizenDTO> convertListToListDto(Collection<Citizen> entityList, Class<CitizenDTO> outClass) {
        if (entityList == null) return Collections.emptyList();
        return entityList.stream()
                .map(entity -> convertToDto(entity, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public List<Citizen> convertListToListEntity(Collection<CitizenDTO> dtoList, Class<Citizen> outClass) {
        if (dtoList == null) return Collections.emptyList();
        return dtoList.stream()
                .map(dto -> convertToEntity(dto, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public Page<CitizenDTO> convertPageToPageDto(Page<Citizen> entityList, Class<CitizenDTO> outClass) {
        if (entityList == null) return Page.empty();
        return entityList.map(element -> modelMapper.map(element, outClass));
    }
}
