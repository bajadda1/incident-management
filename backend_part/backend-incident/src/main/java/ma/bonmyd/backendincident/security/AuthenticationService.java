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
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private final IModelMapper<User, UserResponseDTO> userUserResponseModelMapper;
    private final IModelMapper<Sector, SectorDTO> sectorModelMapper;
    private final IModelMapper<Role, RoleDTO> roleModelMapper;

    //just for professional !!!
    public UserRegisterDTO registerUser(UserRegisterDTO userRegisterDTO) throws MessagingException {
        User isUserExist = this.userRepository.findByUsername(userRegisterDTO.getUsername()).orElse(null);
        if (isUserExist != null) {
            throw new ResourceAlreadyExistsException("user with email already exists");
        }
        User user = this.userUserRegisterModelMapper.convertToEntity(userRegisterDTO, User.class);

//      Role role = this.roleRepository.findById(userRegisterDTO.getRoleDTO().getId()).orElseThrow(() -> new ResourceNotFoundException("not found"));
        Role role = this.roleRepository.findByRole("professional".toLowerCase()).orElseThrow(() -> new ResourceNotFoundException("not found"));
//                this.roleModelMapper.convertToEntity(userRegisterDTO.getRoleDTO(), Role.class);
        user.setRole(role);

        Sector sector = this.sectorRepository.findById(userRegisterDTO.getSectorDTO().getId()).orElseThrow(() -> new ResourceNotFoundException("not found"));
//                this.sectorModelMapper.convertToEntity(userRegisterDTO.getSectorDTO(), Sector.class);
        user.setSector(sector);


        user.setPassword(this.passwordEncoder.encode(userRegisterDTO.getPassword()));

        //by default enable admin
        user.setEnabled(user.getRole().getRole().equalsIgnoreCase("admin"));


        user = this.userRepository.save(user);

        //send a validation email
//        this.sendEmailValidation(user);

        return this.userUserRegisterModelMapper.convertToDto(user, UserRegisterDTO.class);
    }


    public JwtDTO loginUser(UserLoginDTO userLoginDTO) {
        Authentication auth = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userLoginDTO.getUsername(),
                userLoginDTO.getPassword()
        ));

        System.out.println(auth.getAuthorities());
        if (auth == null) {
            System.out.println("??????????|||||||||||| NULL NULL");
            throw new RuntimeException("user mail or pass not correct");
        }
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


    //TODO : email verification
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


}
