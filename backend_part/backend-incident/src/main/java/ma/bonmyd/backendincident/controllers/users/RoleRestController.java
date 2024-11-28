package ma.bonmyd.backendincident.controllers.users;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.bonmyd.backendincident.dtos.users.CitizenDTO;
import ma.bonmyd.backendincident.dtos.users.RoleDTO;
import ma.bonmyd.backendincident.services.users.IRoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${role.api}")
public class RoleRestController {

    private final IRoleService roleService;

    @GetMapping("/{id}")
    public RoleDTO getRoleById(@PathVariable Long id) {
        return this.roleService.getRoleById(id);
    }

    @GetMapping()
//    @PreAuthorize("hasAuthority('ADMIN')")
    public List<RoleDTO> getRoles() {
        return this.roleService.getRoles();
    }

    @PostMapping()
    public RoleDTO getRoleById(@RequestBody RoleDTO roleDTO) {
        return this.roleService.createRole(roleDTO);
    }

    @PutMapping()
    public RoleDTO updateRole(@RequestBody RoleDTO roleDTO) {
        return this.roleService.updateRole(roleDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteRoleById(@Valid @PathVariable Long id) {
        return this.roleService.deleteRoleById(id);
    }


}
