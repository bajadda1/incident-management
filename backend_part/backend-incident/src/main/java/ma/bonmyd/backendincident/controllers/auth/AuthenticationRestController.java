package ma.bonmyd.backendincident.controllers.auth;


import lombok.AllArgsConstructor;
import ma.bonmyd.backendincident.dtos.users.UserLoginDTO;
import ma.bonmyd.backendincident.dtos.users.UserRegisterDTO;
import ma.bonmyd.backendincident.entities.users.User;
import ma.bonmyd.backendincident.security.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${auth.api}")

@AllArgsConstructor
public class AuthenticationRestController {
    private AuthenticationService authenticationService;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        String token = this.authenticationService.loginUser(userLoginDTO);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterDTO> login(@RequestBody UserRegisterDTO user) {
        UserRegisterDTO user1 = this.authenticationService.registerUser(user);
        return ResponseEntity.ok(user1);
    }

    @GetMapping("/me")
    public ResponseEntity<UserRegisterDTO> getCurrentUser() {
        UserRegisterDTO currentUser = authenticationService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

}
