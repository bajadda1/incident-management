package ma.bonmyd.backendincident.services.users.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.bonmyd.backendincident.dtos.users.RoleDTO;
import ma.bonmyd.backendincident.entities.users.Role;
import ma.bonmyd.backendincident.exceptions.ResourceAlreadyExistsException;
import ma.bonmyd.backendincident.exceptions.ResourceNotFoundException;
import ma.bonmyd.backendincident.mappers.IModelMapper;
import ma.bonmyd.backendincident.repositories.users.RoleRepository;
import ma.bonmyd.backendincident.services.users.IRoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final RoleRepository roleRepository;
    private final IModelMapper<Role, RoleDTO> roleModelMapper;

    @Override
    public Role findRoleById(Long id) {
        return this.roleRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("role with id=%d not found", id)));

    }

    @Override
    public Role findRoleByRole(String role) {
        Optional<Role> optionalRole = this.roleRepository.findByRole(role);
        return optionalRole.orElse(null);
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        Role role = this.findRoleById(id);
        return this.roleModelMapper.convertToDto(role, RoleDTO.class);
    }

    @Override
    public RoleDTO getRoleByRole(String role) {
        Role role1 = this.findRoleByRole(role);
        return this.roleModelMapper.convertToDto(role1, RoleDTO.class);
    }

    @Override
    public List<RoleDTO> getRoles() {
        List<Role> roles = this.roleRepository.findAll();
        return this.roleModelMapper.convertListToListDto(roles, RoleDTO.class);
    }

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        Role existingRole = this.findRoleByRole(roleDTO.getRole());
        if (existingRole != null) {
            throw new ResourceAlreadyExistsException(String.format("role with name=%s", roleDTO.getRole()));
        }
        Role role = this.roleModelMapper.convertToEntity(roleDTO, Role.class);
        this.roleRepository.save(role);
        return this.roleModelMapper.convertToDto(role, RoleDTO.class);
    }

    @Override
    public RoleDTO updateRole(RoleDTO roleDTO) {
        Role existingRole = this.findRoleByRole(roleDTO.getRole());
        if (existingRole != null && !existingRole.getId().equals(roleDTO.getId())) {
            throw new ResourceAlreadyExistsException(String.format("role with name=%s", roleDTO.getRole()));
        }
        Role role = this.roleModelMapper.convertToEntity(roleDTO, Role.class);
        this.roleRepository.save(role);
        return this.roleModelMapper.convertToDto(role, RoleDTO.class);
    }

    @Override
    public String deleteRoleById(Long id) {
        Role role = this.findRoleById(id);
        this.roleRepository.delete(role);
        return String.format("role with id=%s has been deleted successfully", id);
    }
}
