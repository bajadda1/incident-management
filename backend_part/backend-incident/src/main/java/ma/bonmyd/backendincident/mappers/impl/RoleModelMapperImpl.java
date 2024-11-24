package ma.bonmyd.backendincident.mappers.impl;


import ma.bonmyd.backendincident.dtos.users.RoleDTO;
import ma.bonmyd.backendincident.entities.users.Role;
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
public class RoleModelMapperImpl implements IModelMapper<Role, RoleDTO> {

    private ModelMapper modelMapper;
    @Autowired
    public RoleModelMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(Conditions.isNotNull());
    }

    @Override
    public RoleDTO convertToDto(Role entity, Class<RoleDTO> dtoClass) {
        if (entity == null) return null;

        return this.modelMapper.map(entity, dtoClass);
    }

    @Override
    public Role convertToEntity(RoleDTO dto, Class<Role> entityClass) {
        if (dto == null) return null;
        return this.modelMapper.map(dto, entityClass);
    }

    @Override
    public List<RoleDTO> convertListToListDto(Collection<Role> entityList, Class<RoleDTO> outClass) {
        if (entityList == null) return Collections.emptyList();
        return entityList.stream()
                .map(entity -> convertToDto(entity, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> convertListToListEntity(Collection<RoleDTO> dtoList, Class<Role> outClass) {
        if (dtoList == null) return Collections.emptyList();
        return dtoList.stream()
                .map(dto -> convertToEntity(dto, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public Page<RoleDTO> convertPageToPageDto(Page<Role> entityList, Class<RoleDTO> outClass) {
        if (entityList == null) return Page.empty();
        return entityList.map(element -> modelMapper.map(element, outClass));
    }
}
