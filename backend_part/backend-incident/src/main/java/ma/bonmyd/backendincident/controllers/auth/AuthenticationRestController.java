package ma.bonmyd.backendincident.controllers.auth;


import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import ma.bonmyd.backendincident.dtos.users.ActivationCodeDTO;
import ma.bonmyd.backendincident.dtos.users.JwtDTO;
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
    public JwtDTO login(@RequestBody UserLoginDTO userLoginDTO) {
        return this.authenticationService.loginUser(userLoginDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterDTO> login(@RequestBody UserRegisterDTO user) throws MessagingException {
        UserRegisterDTO user1 = this.authenticationService.registerUser(user);
        return ResponseEntity.ok(user1);
    }

    @GetMapping("/me")
    public ResponseEntity<UserRegisterDTO> getCurrentUser() {
        UserRegisterDTO currentUser = authenticationService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    @PostMapping("/activate-account")
    public String getCurrentUser(@RequestBody ActivationCodeDTO activationCodeDTO) throws MessagingException {
        return this.authenticationService.activateAccount(activationCodeDTO);
    }

}
