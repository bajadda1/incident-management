package ma.bonmyd.backendincident.dtos.incident;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTO;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncidentDTOPagination {

    private int currentPage;
    private int pageSize;
    private int totalPages;
    private List<IncidentDTO> incidentDTOS;

}
