package ma.bonmyd.backendincident.services.users;

import ma.bonmyd.backendincident.dtos.ApiResponseGenericPagination;
import ma.bonmyd.backendincident.dtos.incident.IncidentDTO;
import ma.bonmyd.backendincident.dtos.users.UserRegisterDTO;
import ma.bonmyd.backendincident.dtos.users.UserResponseDTO;
import ma.bonmyd.backendincident.entities.users.User;
import ma.bonmyd.backendincident.enums.Status;

import java.awt.*;
import java.util.Date;
import java.util.List;

public interface IUserService {


    UserResponseDTO enableProfessionalByEmail(String username);

    UserResponseDTO disableProfessionalByEmail(String username);

    UserResponseDTO enableProfessionalById(Long id);

    UserResponseDTO disableProfessionalById(Long id);

    UserResponseDTO getUserById(Long id);

    UserResponseDTO getCurrentUser();

    ApiResponseGenericPagination<UserResponseDTO> getFilteredUsers(Boolean enabled,
                                                                   Long sectorId,
                                                                   String fullname,
                                                                   String username,
                                                                   int page, int size);

    String deleteProfessional(Long id);
}
