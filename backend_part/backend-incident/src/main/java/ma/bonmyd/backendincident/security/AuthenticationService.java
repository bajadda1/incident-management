package ma.bonmyd.backendincident.security;


import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.bonmyd.backendincident.dtos.incident.SectorDTO;
import ma.bonmyd.backendincident.dtos.users.*;
import ma.bonmyd.backendincident.email.EmailService;
import ma.bonmyd.backendincident.entities.incident.Sector;
import ma.bonmyd.backendincident.entities.users.Role;
import ma.bonmyd.backendincident.entities.users.Token;
import ma.bonmyd.backendincident.entities.users.User;
import ma.bonmyd.backendincident.enums.EmailTemplateName;
import ma.bonmyd.backendincident.exceptions.ResourceAlreadyExistsException;
import ma.bonmyd.backendincident.exceptions.ResourceNotFoundException;
import ma.bonmyd.backendincident.mappers.IModelMapper;
import ma.bonmyd.backendincident.repositories.incident.SectorRepository;
import ma.bonmyd.backendincident.repositories.users.RoleRepository;
import ma.bonmyd.backendincident.repositories.users.TokenRepository;
import ma.bonmyd.backendincident.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    @Value("${spring.mail.frontend.url}")
    private String activationURL;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final SectorRepository sectorRepository;
    private final TokenRepository tokenRepository;

    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;


    private final IModelMapper<User, UserRegisterDTO> userUserRegisterModelMapper;
    private final IModelMapper<Sector, SectorDTO> sectorModelMapper;
    private final IModelMapper<Role, RoleDTO> roleModelMapper;

    //just for professional !!!
    public UserRegisterDTO registerUser(UserRegisterDTO userRegisterDTO) throws MessagingException {
        User isUserExist = this.userRepository.findByUsername(userRegisterDTO.getUsername()).orElse(null);
        if (isUserExist != null) {
            throw new ResourceAlreadyExistsException("user with email already exists");
        }
        User user = this.userUserRegisterModelMapper.convertToEntity(userRegisterDTO, User.class);

        Role role = this.roleModelMapper.convertToEntity(userRegisterDTO.getRoleDTO(), Role.class);
        user.setRole(role);

        Sector sector = this.sectorModelMapper.convertToEntity(userRegisterDTO.getSectorDTO(), Sector.class);
        user.setSector(sector);


        user.setPassword(this.passwordEncoder.encode(userRegisterDTO.getPassword()));

        //by default
        user.setEnabled(false);
        user = this.userRepository.save(user);

        //send a validation email
        this.sendEmailValidation(user);

        return this.userUserRegisterModelMapper.convertToDto(user, UserRegisterDTO.class);
    }

    private void sendEmailValidation(User user) throws MessagingException {
        String newToken = this.generateAndSaveActivationToken(user);
        this.emailService.sendEmail(
                user.getUsername(),
                user.getFullname(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationURL,
                newToken,
                "account activation"

        );
    }

    private String generateAndSaveActivationToken(User user) {
        //generate activation code (6 digit)
        //token in our case is the activation code
        String activationCode = generateActivationCode(4);

        Token token = Token
                .builder()
                .token(activationCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        //save the token
        this.tokenRepository.save(token);
        return activationCode;
    }

    private String generateActivationCode(int length) {
        String numbers = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(numbers.length());//0..9 randomly
            codeBuilder.append(numbers.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public JwtDTO loginUser(UserLoginDTO userLoginDTO) {
        // Fetch the user by email
//        User user = userRepository.findByUsername(userLoginDTO.getUsername())
//                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
//
//        // Check if the password matches
//        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Invalid email or password");
//        }
//
//        if (!user.isEnabled()) {
//            throw new RuntimeException("user not  enabled yet");
//        }

        Authentication auth = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userLoginDTO.getUsername(),
                userLoginDTO.getPassword()
        ));

        User user = (User) auth.getPrincipal();

        if (!user.isEnabled()) {
            throw new RuntimeException("user not  enabled yet,try to activate your account");
        }
        return JwtDTO
                .builder()
                // Generate the JWT
                .jwt(jwtService.generateToken(Map.of("fullname", user.getFullname()), user))
                .build();
    }

    public UserRegisterDTO getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(SecurityContextHolder.getContext().getAuthentication());

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            // Fetch the user from the database if needed
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return this.userUserRegisterModelMapper.convertToDto(user, UserRegisterDTO.class);
        } else {
            throw new RuntimeException("No authenticated user");
        }
    }

    public String activateAccount(ActivationCodeDTO activationCodeDTO) throws MessagingException {
        String activationCode = activationCodeDTO.getActivationCode();
        Token token = this.tokenRepository.findByToken(activationCode).orElseThrow(
                () -> new ResourceNotFoundException("invalid activation code")
        );

        //check if the code activation has expired?

        if (LocalDateTime.now().isAfter(token.getExpiresAt())) {
            //resend the activation code mail
            this.sendEmailValidation(token.getUser());

            return "activation code has been expired, a new one has been sent to the same email, check and try agin!";
        }

        User user = token.getUser();

        //after code activation check ,set  the user enabled to true
        user.setEnabled(true);

        token.setValidatedAt(LocalDateTime.now());
        this.tokenRepository.save(token);
        return "account activation is done successfully";

    }

}
