package ma.bonmyd.backendincident.dtos.users;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.bonmyd.backendincident.dtos.incident.SectorDTO;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterDTO {
    private Long id;
    private String username;
    //email unique !
    private String email;
    private String password;
    private RoleDTO roleDTO;
    private SectorDTO sectorDTO;
    private boolean enabled;
}
