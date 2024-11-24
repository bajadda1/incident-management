package ma.bonmyd.backendincident.dtos.incident;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.bonmyd.backendincident.entities.incident.Incident;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RejectionDTO {

    private Long id;
    private String reason;
    private Date date;
    
}
