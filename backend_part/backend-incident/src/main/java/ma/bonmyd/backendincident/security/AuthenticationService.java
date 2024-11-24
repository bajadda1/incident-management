package ma.bonmyd.backendincident.security;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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

    public UserRegisterDTO registerUser(UserRegisterDTO userRegisterDTO){
        User isUserExist=this.userRepository.findByEmail(userRegisterDTO.getEmail()).orElse(null);
        if (isUserExist!=null){
            throw new ResourceAlreadyExistsException("user with email already exists");
        }
        Role role = roleRepository.findById(userRegisterDTO.getRoleDTO().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        Sector sector = sectorRepository.findById(userRegisterDTO.getSectorDTO().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sector not found"));

        User user=this.userUserRegisterModelMapper.convertToEntity(userRegisterDTO,User.class);
        user.setPassword(this.passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setRole(role);
        user.setSector(sector);
        user=this.userRepository.save(user);

        return this.userUserRegisterModelMapper.convertToDto(user,UserRegisterDTO.class);
    }

    public String loginUser(UserLoginDTO userLoginDTO) {
        // Fetch the user by email
        User user = userRepository.findByEmail(userLoginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check if the password matches
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate the JWT
        return jwtService.generateToken(user);
    }

    public UserRegisterDTO getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            // Fetch the user from the database if needed
            User user=userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return this.userUserRegisterModelMapper.convertToDto(user,UserRegisterDTO.class);
        } else {
            throw new RuntimeException("No authenticated user");
        }
    }

}
