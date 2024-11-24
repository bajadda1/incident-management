package ma.bonmyd.backendincident.dtos.incident;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.bonmyd.backendincident.dtos.users.CitizenDTO;
import ma.bonmyd.backendincident.enums.Status;

import java.util.Date;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IncidentUpdateDTO {
    private Long id;
    private Date updatedAt;
    private String description;
    private TypeDTO typeDTO;
    private SectorDTO sectorDTO;
}
