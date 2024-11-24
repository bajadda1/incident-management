package ma.bonmyd.backendincident.dtos.territoriale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.bonmyd.backendincident.dtos.incident.IncidentDTO;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegionDTOPagination {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private List<RegionDTO> regionDTOS;
}
