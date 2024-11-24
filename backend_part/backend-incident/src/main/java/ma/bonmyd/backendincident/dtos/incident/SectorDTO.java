package ma.bonmyd.backendincident.dtos.incident;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SectorDTO {
    private Long id;
    private String name;
}
