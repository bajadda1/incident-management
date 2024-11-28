package ma.bonmyd.backendincident.services.users;

import ma.bonmyd.backendincident.dtos.users.RoleDTO;
import ma.bonmyd.backendincident.entities.users.Role;

import java.util.List;

public interface IRoleService {

    Role findRoleById(Long id);

    Role findRoleByRole(String role);

    RoleDTO getRoleById(Long id);

    RoleDTO getRoleByRole(String role);

    List<RoleDTO> getRoles();

    RoleDTO createRole(RoleDTO roleDTO);

    RoleDTO updateRole(RoleDTO roleDTO);

    String deleteRoleById(Long id);
}
