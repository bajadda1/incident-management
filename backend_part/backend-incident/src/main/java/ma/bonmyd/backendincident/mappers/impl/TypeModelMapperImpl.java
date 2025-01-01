package ma.bonmyd.backendincident.mappers.impl;

import ma.bonmyd.backendincident.dtos.incident.SectorDTO;
import ma.bonmyd.backendincident.dtos.incident.TypeDTO;
import ma.bonmyd.backendincident.entities.incident.Sector;
import ma.bonmyd.backendincident.entities.incident.Type;
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
public class TypeModelMapperImpl implements IModelMapper<Type, TypeDTO> {

    private ModelMapper modelMapper;
    private IModelMapper<Sector, SectorDTO> sectorModelMapper;

    @Autowired
    public TypeModelMapperImpl(ModelMapper modelMapper, IModelMapper<Sector, SectorDTO> sectorModelMapper) {
        this.modelMapper = modelMapper;
        this.sectorModelMapper = sectorModelMapper;
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(Conditions.isNotNull());
    }

    @Override
    public TypeDTO convertToDto(Type entity, Class<TypeDTO> dtoClass) {
        if (entity == null) return null;
        Sector sector = entity.getSector();
        TypeDTO typeDTO = this.modelMapper.map(entity, dtoClass);
        SectorDTO sectorDTO = this.sectorModelMapper.convertToDto(sector, SectorDTO.class);
        typeDTO.setSectorDTO(sectorDTO);
        return typeDTO;
    }

    @Override
    public Type convertToEntity(TypeDTO dto, Class<Type> entityClass) {
        if (dto == null) return null;
        return this.modelMapper.map(dto, entityClass);
    }

    @Override
    public List<TypeDTO> convertListToListDto(Collection<Type> entityList, Class<TypeDTO> outClass) {
        if (entityList == null) return Collections.emptyList();
        return entityList.stream()
                .map(entity -> convertToDto(entity, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public List<Type> convertListToListEntity(Collection<TypeDTO> dtoList, Class<Type> outClass) {
        if (dtoList == null) return Collections.emptyList();
        return dtoList.stream()
                .map(dto -> convertToEntity(dto, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public Page<TypeDTO> convertPageToPageDto(Page<Type> entityList, Class<TypeDTO> outClass) {
        if (entityList == null) return Page.empty();
        return entityList.map(element -> modelMapper.map(element, outClass));
    }
}
