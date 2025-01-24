package ma.bonmyd.backendincident.dtos.incident;

import lombok.AllArgsConstructor;
import lombok.Data;
import ma.bonmyd.backendincident.enums.Status;

@Data
@AllArgsConstructor
public class IncidentStatusGroupDTO {
    private Status status; // Status as a String
    private Long count;    // Count of incidents for this status
}
