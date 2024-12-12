package ma.bonmyd.backendincident.dtos.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.bonmyd.backendincident.dtos.incident.SectorDTO;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String fullname;
    //email unique !
    private String username;
    private RoleDTO roleDTO;
    private SectorDTO sectorDTO;
    private boolean enabled;
}
