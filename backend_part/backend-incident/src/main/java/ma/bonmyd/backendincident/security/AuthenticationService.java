package ma.bonmyd.backendincident.security;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import ma.bonmyd.backendincident.dtos.incident.SectorDTO;
import ma.bonmyd.backendincident.dtos.users.JwtDTO;
import ma.bonmyd.backendincident.dtos.users.RoleDTO;
import ma.bonmyd.backendincident.dtos.users.UserLoginDTO;
import ma.bonmyd.backendincident.dtos.users.UserRegisterDTO;
import ma.bonmyd.backendincident.entities.incident.Sector;
import ma.bonmyd.backendincident.entities.users.Role;
import ma.bonmyd.backendincident.entities.users.User;
import ma.bonmyd.backendincident.exceptions.ResourceAlreadyExistsException;
import ma.bonmyd.backendincident.exceptions.ResourceNotFoundException;
import ma.bonmyd.backendincident.mappers.IModelMapper;
import ma.bonmyd.backendincident.repositories.incident.SectorRepository;
import ma.bonmyd.backendincident.repositories.users.RoleRepository;
import ma.bonmyd.backendincident.repositories.users.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
@Transactional
public class AuthenticationService {

    private UserRepository userRepository;
    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    private SectorRepository sectorRepository;

    private IModelMapper<User, UserRegisterDTO> userUserRegisterModelMapper;
    private IModelMapper<Sector, SectorDTO> sectorModelMapper;
    private IModelMapper<Role, RoleDTO> roleModelMapper;

    public UserRegisterDTO registerUser(UserRegisterDTO userRegisterDTO){
        User isUserExist=this.userRepository.findByUsername(userRegisterDTO.getUsername()).orElse(null);
        if (isUserExist!=null){
            throw new ResourceAlreadyExistsException("user with email already exists");
        }
        User user=this.userUserRegisterModelMapper.convertToEntity(userRegisterDTO,User.class);

        Role role=this.roleModelMapper.convertToEntity(userRegisterDTO.getRoleDTO(),Role.class);
        user.setRole(role);

        Sector sector=this.sectorModelMapper.convertToEntity(userRegisterDTO.getSectorDTO(),Sector.class);
        user.setSector(sector);


        user.setPassword(this.passwordEncoder.encode(userRegisterDTO.getPassword()));


        user=this.userRepository.save(user);

        return this.userUserRegisterModelMapper.convertToDto(user,UserRegisterDTO.class);
    }

    public JwtDTO loginUser(UserLoginDTO userLoginDTO) {
        // Fetch the user by email
        User user = userRepository.findByUsername(userLoginDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check if the password matches
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!user.isEnabled()){
            throw new RuntimeException("user not  enabled yet");
        }

        return JwtDTO
                .builder()
                // Generate the JWT
                .jwt(jwtService.generateToken(Map.of("fullname",user.getFullname()),user))
                .build();
    }

    public UserRegisterDTO getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(SecurityContextHolder.getContext().getAuthentication());

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            // Fetch the user from the database if needed
            User user=userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return this.userUserRegisterModelMapper.convertToDto(user,UserRegisterDTO.class);
        } else {
            throw new RuntimeException("No authenticated user");
        }
    }

}
