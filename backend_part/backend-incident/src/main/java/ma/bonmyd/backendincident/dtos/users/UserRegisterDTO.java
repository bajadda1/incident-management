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
    private String fullname;
    //email unique !
    private String username;
    private String password;
    private RoleDTO roleDTO;
    private SectorDTO sectorDTO;
//    private boolean enabled;
}
