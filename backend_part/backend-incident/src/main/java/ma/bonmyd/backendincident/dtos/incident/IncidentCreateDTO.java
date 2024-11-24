package ma.bonmyd.backendincident.dtos.incident;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.bonmyd.backendincident.dtos.users.CitizenDTO;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentCreateDTO {
    private String description;
    @NotNull(message = "type could not be null")
    private TypeDTO typeDTO;
    @NotNull(message = "sector could not be null")
    private SectorDTO sectorDTO;
    @NotNull(message = "location could not be null")
    private String location;
    //the instance imei
    //register the citizen at the first declaration
    private String citizenIMEI;
}
