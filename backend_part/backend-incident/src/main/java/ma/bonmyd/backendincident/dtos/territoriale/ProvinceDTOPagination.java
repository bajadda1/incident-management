package ma.bonmyd.backendincident.dtos.territoriale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceDTOPagination {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    List<ProvinceDTO> provinceDTOS;
}
