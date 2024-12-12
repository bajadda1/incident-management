package ma.bonmyd.backendincident.services.users.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.bonmyd.backendincident.dtos.ApiResponseGenericPagination;
import ma.bonmyd.backendincident.dtos.incident.SectorDTO;
import ma.bonmyd.backendincident.dtos.users.RoleDTO;
import ma.bonmyd.backendincident.dtos.users.UserRegisterDTO;
import ma.bonmyd.backendincident.dtos.users.UserResponseDTO;
import ma.bonmyd.backendincident.entities.incident.Sector;
import ma.bonmyd.backendincident.entities.users.Role;
import ma.bonmyd.backendincident.entities.users.User;
import ma.bonmyd.backendincident.exceptions.ResourceNotFoundException;
import ma.bonmyd.backendincident.mappers.IModelMapper;
import ma.bonmyd.backendincident.repositories.incident.SectorRepository;
import ma.bonmyd.backendincident.repositories.users.RoleRepository;
import ma.bonmyd.backendincident.repositories.users.UserRepository;
import ma.bonmyd.backendincident.services.users.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
    public ApiResponseGenericPagination<UserResponseDTO> getAllUsers() {
        return null;
    }

    @Override
    public UserResponseDTO enableProfessionalByEmail(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(true);
        return this.userUserResponseModelMapper.convertToDto(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO disableProfessionalByEmail(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(false);
        return this.userUserResponseModelMapper.convertToDto(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO enableProfessionalById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(true);
        return this.userUserResponseModelMapper.convertToDto(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO disableProfessionalById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(false);
        return this.userUserResponseModelMapper.convertToDto(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return this.userUserResponseModelMapper.convertToDto(user, UserResponseDTO.class);
    }

    @Override
    public ApiResponseGenericPagination<UserResponseDTO> getAllProfessionalsPagination(int currentPage, int size) {
        Pageable pageable = PageRequest.of(currentPage, size);
        Page<User> userPage = userRepository.findByRole("professional", pageable);

        // Convert to DTO
        List<UserResponseDTO> professionalDTOs = this.userUserResponseModelMapper.convertListToListDto(
                userPage.getContent(), UserResponseDTO.class);

        // Build response
        return ApiResponseGenericPagination.<UserResponseDTO>builder()
                .currentPage(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalPages(userPage.getTotalPages())
                .list(professionalDTOs)
                .build();
    }

    @Override
    public List<UserResponseDTO> getAllProfessionals() {
        List<User> users = this.userRepository.findAll()
                .stream()
                .filter(user -> user.getRole().getRole().equalsIgnoreCase("professional"))
                .collect(Collectors.toList());
        return this.userUserResponseModelMapper.convertListToListDto(users, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            //get the username (email)
            String username = ((UserDetails) principal).getUsername();
            // Fetch the user from the database if needed
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return this.userUserResponseModelMapper.convertToDto(user, UserResponseDTO.class);
        } else {
            throw new RuntimeException("No authenticated user");
        }
    }
}
