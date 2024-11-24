package ma.bonmyd.backendincident.dtos.territoriale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegionDTO {
    private Long id;
    private String name;
    private double area;
    private double perimeter;
    private String geom;
}
