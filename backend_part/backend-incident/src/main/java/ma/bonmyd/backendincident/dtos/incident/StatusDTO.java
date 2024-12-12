package ma.bonmyd.backendincident.dtos.incident;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.bonmyd.backendincident.enums.Status;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusDTO {
    public Status status;
}
