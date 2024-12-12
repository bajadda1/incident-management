package ma.bonmyd.backendincident.services.users;

import ma.bonmyd.backendincident.dtos.ApiResponseGenericPagination;
import ma.bonmyd.backendincident.dtos.users.UserRegisterDTO;
import ma.bonmyd.backendincident.dtos.users.UserResponseDTO;
import ma.bonmyd.backendincident.entities.users.User;

import java.awt.*;
import java.util.List;

public interface IUserService {


    ApiResponseGenericPagination<UserResponseDTO> getAllUsers();

    UserResponseDTO enableProfessionalByEmail(String username);
    UserResponseDTO disableProfessionalByEmail(String username);

    UserResponseDTO enableProfessionalById(Long id);
    UserResponseDTO disableProfessionalById(Long id);

    UserResponseDTO getUserById(Long id);

    ApiResponseGenericPagination<UserResponseDTO> getAllProfessionalsPagination(int currentPage, int size);

    List<UserResponseDTO> getAllProfessionals();

    UserResponseDTO getCurrentUser();
}
