package ma.bonmyd.backendincident.mappers.impl;


import ma.bonmyd.backendincident.dtos.incident.SectorDTO;
import ma.bonmyd.backendincident.dtos.users.RoleDTO;
import ma.bonmyd.backendincident.dtos.users.UserResponseDTO;
import ma.bonmyd.backendincident.entities.incident.Sector;
import ma.bonmyd.backendincident.entities.users.Role;
import ma.bonmyd.backendincident.entities.users.User;
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
public class UserResponseDTOModelMapperImpl implements IModelMapper<User, UserResponseDTO> {

    private ModelMapper modelMapper;
    private IModelMapper<Sector, SectorDTO> sectorModelMapper;
    private IModelMapper<Role, RoleDTO> roleModelMapper;
    @Autowired
    public UserResponseDTOModelMapperImpl(ModelMapper modelMapper, IModelMapper<Sector, SectorDTO> sectorModelMapper, IModelMapper<Role, RoleDTO> roleModelMapper) {
        this.modelMapper = modelMapper;
        this.sectorModelMapper = sectorModelMapper;
        this.roleModelMapper = roleModelMapper;
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(Conditions.isNotNull());
    }

    @Override
    public UserResponseDTO convertToDto(User entity, Class<UserResponseDTO> dtoClass) {
        if (entity == null) return null;
        SectorDTO sectorDTO=this.sectorModelMapper.convertToDto(entity.getSector(),SectorDTO.class);
        RoleDTO roleDTO=this.roleModelMapper.convertToDto(entity.getRole(), RoleDTO.class);
        UserResponseDTO userRegisterDTO=this.modelMapper.map(entity, dtoClass);
        userRegisterDTO.setRoleDTO(roleDTO);
        userRegisterDTO.setSectorDTO(sectorDTO);
        return userRegisterDTO;
    }

    @Override
    public User convertToEntity(UserResponseDTO dto, Class<User> entityClass) {
        if (dto == null) return null;
        Sector sector=this.sectorModelMapper.convertToEntity(dto.getSectorDTO(),Sector.class);
        Role role=this.roleModelMapper.convertToEntity(dto.getRoleDTO(),Role.class);
        User user=this.modelMapper.map(dto, entityClass);
        user.setRole(role);
        user.setSector(sector);
        return user;
    }

    @Override
    public List<UserResponseDTO> convertListToListDto(Collection<User> entityList, Class<UserResponseDTO> outClass) {
        if (entityList == null) return Collections.emptyList();
        return entityList.stream()
                .map(entity -> convertToDto(entity, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> convertListToListEntity(Collection<UserResponseDTO> dtoList, Class<User> outClass) {
        if (dtoList == null) return Collections.emptyList();
        return dtoList.stream()
                .map(dto -> convertToEntity(dto, outClass))
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserResponseDTO> convertPageToPageDto(Page<User> entityList, Class<UserResponseDTO> outClass) {
        if (entityList == null) return Page.empty();
        return entityList.map(element -> modelMapper.map(element, outClass));
    }
}
