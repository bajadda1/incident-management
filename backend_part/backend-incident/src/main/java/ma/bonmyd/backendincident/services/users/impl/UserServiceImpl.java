package ma.bonmyd.backendincident.services.users.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.bonmyd.backendincident.dtos.ApiResponseGenericPagination;
import ma.bonmyd.backendincident.dtos.incident.IncidentDTO;
import ma.bonmyd.backendincident.dtos.incident.SectorDTO;
import ma.bonmyd.backendincident.dtos.users.RoleDTO;
import ma.bonmyd.backendincident.dtos.users.UserRegisterDTO;
import ma.bonmyd.backendincident.dtos.users.UserResponseDTO;
import ma.bonmyd.backendincident.entities.incident.Incident;
import ma.bonmyd.backendincident.entities.incident.Sector;
import ma.bonmyd.backendincident.entities.users.Role;
import ma.bonmyd.backendincident.entities.users.User;
import ma.bonmyd.backendincident.enums.Status;
import ma.bonmyd.backendincident.exceptions.ResourceNotFoundException;
import ma.bonmyd.backendincident.mappers.IModelMapper;
import ma.bonmyd.backendincident.repositories.incident.SectorRepository;
import ma.bonmyd.backendincident.repositories.users.RoleRepository;
import ma.bonmyd.backendincident.repositories.users.UserRepository;
import ma.bonmyd.backendincident.services.users.IUserService;
import ma.bonmyd.backendincident.specifications.IncidentSpecification;
import ma.bonmyd.backendincident.specifications.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SectorRepository sectorRepository;

    private final IModelMapper<User, UserResponseDTO> userUserResponseModelMapper;
    private final IModelMapper<Sector, SectorDTO> sectorModelMapper;
    private final IModelMapper<Role, RoleDTO> roleModelMapper;

    private final AuthenticationManager authenticationManager;


    @Override
    public UserResponseDTO enableProfessionalByEmail(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(true);
        return this.userUserResponseModelMapper.convertToDto(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO disableProfessionalByEmail(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(false);
        return this.userUserResponseModelMapper.convertToDto(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO enableProfessionalById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(true);
        return this.userUserResponseModelMapper.convertToDto(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO disableProfessionalById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(false);
        return this.userUserResponseModelMapper.convertToDto(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return this.userUserResponseModelMapper.convertToDto(user, UserResponseDTO.class);
    }


    @Override
    public UserResponseDTO getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            //get the username (email)
            String username = ((UserDetails) principal).getUsername();
            // Fetch the user from the database if needed
            User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return this.userUserResponseModelMapper.convertToDto(user, UserResponseDTO.class);
        } else {
            throw new RuntimeException("No authenticated user");
        }
    }

    @Override
    public ApiResponseGenericPagination<UserResponseDTO> getFilteredUsers(Boolean enabled, Long sectorId, String fullname, String username, int page, int size) {
        String role = "professional";

        Specification<User> userSpecification = Specification.where(UserSpecification.hasRole(role))
                .and(UserSpecification.hasSectorId(sectorId))
                .and(UserSpecification.isEnabled(enabled))
                .and(UserSpecification.fullnameContains(fullname))
                .and(UserSpecification.usernameContains(username));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> userPage = this.userRepository.findAll(userSpecification, pageable);

        // Map the Page of entities to a Page of DTOs
        Page<UserResponseDTO> userResponseDTOS = userUserResponseModelMapper.convertPageToPageDto(userPage, UserResponseDTO.class);


        // Build response
        return ApiResponseGenericPagination.<UserResponseDTO>builder().currentPage(userResponseDTOS.getNumber()).pageSize(userResponseDTOS.getSize()).totalPages(userResponseDTOS.getTotalPages()).totalElements(userResponseDTOS.getTotalElements()).list(userResponseDTOS.getContent()).sliceTotalElements(userResponseDTOS.getNumberOfElements()).build();

    }

    @Override
    public String deleteProfessional(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        this.userRepository.deleteById(id);
        return "user with id:" + user.getId() + " successfully deleted";
    }

}
