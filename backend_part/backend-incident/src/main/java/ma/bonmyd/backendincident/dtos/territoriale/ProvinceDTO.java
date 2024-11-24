package ma.bonmyd.backendincident.dtos.territoriale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceDTO {
    private Long id;
    private String name;
    private double area;
    private double perimeter;
//    private RegionDTO regionDTO;
    private String geom;
}
