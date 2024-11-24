package ma.bonmyd.backendincident.dtos.incident;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.bonmyd.backendincident.dtos.users.CitizenDTO;
import ma.bonmyd.backendincident.enums.Status;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentDTO {
    private Long id;
    private String photo;
    private Date createdAt;
    private Date updatedAt;
    private String description;
    private Status status;
    private TypeDTO typeDTO;
    private SectorDTO sectorDTO;
    private String location;
//    private CitizenDTO citizenDTO;
}
