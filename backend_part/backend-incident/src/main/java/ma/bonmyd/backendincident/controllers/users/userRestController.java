package ma.bonmyd.backendincident.controllers.users;


import lombok.RequiredArgsConstructor;
import ma.bonmyd.backendincident.dtos.ApiResponseGenericPagination;
import ma.bonmyd.backendincident.dtos.users.UserResponseDTO;
import ma.bonmyd.backendincident.services.users.IUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${user.api}")
public class userRestController {

    final private IUserService userService;

    //Need Auth only
    @GetMapping("/me")
    public UserResponseDTO getCurrentUser() {
        return this.userService.getCurrentUser();
    }

    //Auth + Admin role ==============
    @PostMapping("/enable-account")
    public UserResponseDTO enableUserByEmail(@RequestBody String username) {
        return this.userService.enableProfessionalByEmail(username);
    }

    //Auth + Admin role ==============
    @PostMapping("/disable-account")
    public UserResponseDTO disableProfessionalByEmail(@RequestBody String username) {
        return this.userService.disableProfessionalByEmail(username);
    }

    //Auth + Admin role ============== help admin to handle a specific professional user
    @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable Long id) {
        return this.userService.getUserById(id);
    }

    //Auth + Admin role ==============
    @PostMapping("/enable-account/{id}")
    public UserResponseDTO enableUserById(@PathVariable Long id) {
        return this.userService.enableProfessionalById(id);
    }

    //Auth + Admin role ==============
    @PostMapping("/disable-account/{id}")
    public UserResponseDTO disableProfessionalById(@PathVariable Long id) {
        return this.userService.disableProfessionalById(id);
    }

    //Auth + Admin role ==============
    @GetMapping()
    public List<UserResponseDTO> getProfessionals() {
        return this.userService.getAllProfessionals();
    }

    //Auth + Admin role ==============
    @GetMapping("/pagination")
    public ApiResponseGenericPagination<UserResponseDTO> getProfessionals(@RequestParam(name = "current", defaultValue = "${default.current.page}") int current, @RequestParam(name = "size", defaultValue = "${default.page.size}") int size) {
        return this.userService.getAllProfessionalsPagination(current, size);
    }

}
