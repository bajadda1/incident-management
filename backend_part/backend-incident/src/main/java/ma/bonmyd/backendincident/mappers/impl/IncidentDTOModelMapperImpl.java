package ma.bonmyd.backendincident.mappers.impl;

import ma.bonmyd.backendincident.dtos.incident.IncidentDTO;
import ma.bonmyd.backendincident.dtos.incident.SectorDTO;
import ma.bonmyd.backendincident.dtos.incident.TypeDTO;
import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTO;
import ma.bonmyd.backendincident.dtos.users.CitizenDTO;
import ma.bonmyd.backendincident.entities.incident.Incident;
import ma.bonmyd.backendincident.entities.incident.Sector;
import ma.bonmyd.backendincident.entities.incident.Type;
import ma.bonmyd.backendincident.entities.territoriale.Province;
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
public class IncidentDTOModelMapperImpl implements IModelMapper<Incident, IncidentDTO> {

    private final ModelMapper modelMapper;
    private final IModelMapper<Type, TypeDTO> typeModelMapper;
    private final IModelMapper<Sector, SectorDTO> sectorModelMapper;
    private final IModelMapper<Province, ProvinceDTO> provinceModelMapper;

    @Autowired
    public IncidentDTOModelMapperImpl(ModelMapper modelMapper, IModelMapper<Type, TypeDTO> typeModelMapper, IModelMapper<Sector, SectorDTO> sectorModelMapper, IModelMapper<Province, ProvinceDTO> provinceModelMapper) {
        this.modelMapper = modelMapper;
        this.typeModelMapper = typeModelMapper;
        this.sectorModelMapper = sectorModelMapper;
        this.provinceModelMapper = provinceModelMapper;
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(Conditions.isNotNull());
    }

    @Override
    public IncidentDTO convertToDto(Incident entity, Class<IncidentDTO> dtoClass) {
        if (entity == null) return null;
        Type type = entity.getType();
        Sector sector = entity.getSector();
        Province province = entity.getProvince();
        IncidentDTO incidentDTO = this.modelMapper.map(entity, dtoClass);
        TypeDTO typeDTO = this.typeModelMapper.convertToDto(type, TypeDTO.class);
        SectorDTO sectorDTO = this.sectorModelMapper.convertToDto(sector, SectorDTO.class);
        ProvinceDTO provinceDTO = this.provinceModelMapper.convertToDto(province, ProvinceDTO.class);
        incidentDTO.setSectorDTO(sectorDTO);
        incidentDTO.setTypeDTO(typeDTO);
        incidentDTO.setProvinceDTO(provinceDTO);
        return incidentDTO;
    }

    @Override
    public Incident convertToEntity(IncidentDTO dto, Class<Incident> entityClass) {
        if (dto == null) return null;
        TypeDTO typeDTO = dto.getTypeDTO();
        SectorDTO sectorDTO = dto.getSectorDTO();
        Type type = this.typeModelMapper.convertToEntity(typeDTO, Type.class);
        Sector sector = this.sectorModelMapper.convertToEntity(sectorDTO, Sector.class);
        Incident incident = this.modelMapper.map(dto, entityClass);
        incident.setSector(sector);
        incident.setType(type);
        return incident;
    }

    @Override
    public List<IncidentDTO> convertListToListDto(Collection<Incident> entityList, Class<IncidentDTO> outClass) {
        if (entityList == null) return Collections.emptyList();
        return entityList.stream()
                .map(entity -> convertToDto(entity, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public List<Incident> convertListToListEntity(Collection<IncidentDTO> dtoList, Class<Incident> outClass) {
        if (dtoList == null) return Collections.emptyList();
        return dtoList.stream()
                .map(dto -> convertToEntity(dto, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public Page<IncidentDTO> convertPageToPageDto(Page<Incident> entityList, Class<IncidentDTO> outClass) {
        if (entityList == null) return Page.empty();
        // Map each Incident to IncidentDTO using the custom convertToDto method
        return entityList.map(element -> convertToDto(element, outClass));
    }
}
