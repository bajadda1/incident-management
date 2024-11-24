package ma.bonmyd.backendincident.mappers.impl;

import ma.bonmyd.backendincident.dtos.incident.IncidentCreateDTO;
import ma.bonmyd.backendincident.dtos.incident.SectorDTO;
import ma.bonmyd.backendincident.dtos.incident.TypeDTO;
import ma.bonmyd.backendincident.dtos.users.CitizenDTO;
import ma.bonmyd.backendincident.entities.incident.Incident;
import ma.bonmyd.backendincident.entities.incident.Sector;
import ma.bonmyd.backendincident.entities.incident.Type;
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
public class IncidentCreateDTOModelMapperImpl implements IModelMapper<Incident, IncidentCreateDTO> {

    private ModelMapper modelMapper;
    private IModelMapper<Citizen, CitizenDTO> citizenModelMapper;
    private IModelMapper<Type, TypeDTO> typeModelMapper;
    private IModelMapper<Sector, SectorDTO> sectorModelMapper;

    @Autowired
    public IncidentCreateDTOModelMapperImpl(ModelMapper modelMapper, IModelMapper<Citizen, CitizenDTO> citizenModelMapper, IModelMapper<Type, TypeDTO> typeModelMapper, IModelMapper<Sector, SectorDTO> sectorModelMapper) {
        this.modelMapper = modelMapper;
        this.citizenModelMapper = citizenModelMapper;
        this.typeModelMapper = typeModelMapper;
        this.sectorModelMapper = sectorModelMapper;
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(Conditions.isNotNull());
    }

    @Override
    public IncidentCreateDTO convertToDto(Incident entity, Class<IncidentCreateDTO> dtoClass) {
        if (entity == null) return null;

        return this.modelMapper.map(entity, dtoClass);
    }

    @Override
    public Incident convertToEntity(IncidentCreateDTO dto, Class<Incident> entityClass) {
        if (dto == null) return null;
        TypeDTO typeDTO = dto.getTypeDTO();
        SectorDTO sectorDTO = dto.getSectorDTO();
        Type type = this.typeModelMapper.convertToEntity(typeDTO, Type.class);
        Sector sector = this.sectorModelMapper.convertToEntity(sectorDTO, Sector.class);
        type.setSector(sector);
        Incident incident = this.modelMapper.map(dto, entityClass);
        incident.setSector(sector);
        incident.setType(type);
        // Set location and citizenIMEI explicitly if they’re not handled by ModelMapper
        incident.setLocation(dto.getLocation());
        return incident;
    }

    @Override
    public List<IncidentCreateDTO> convertListToListDto(Collection<Incident> entityList, Class<IncidentCreateDTO> outClass) {
        if (entityList == null) return Collections.emptyList();
        return entityList.stream()
                .map(entity -> convertToDto(entity, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public List<Incident> convertListToListEntity(Collection<IncidentCreateDTO> dtoList, Class<Incident> outClass) {
        if (dtoList == null) return Collections.emptyList();
        return dtoList.stream()
                .map(dto -> convertToEntity(dto, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public Page<IncidentCreateDTO> convertPageToPageDto(Page<Incident> entityList, Class<IncidentCreateDTO> outClass) {
        if (entityList == null) return Page.empty();
        return entityList.map(element -> modelMapper.map(element, outClass));
    }
}
