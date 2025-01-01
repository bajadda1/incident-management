package ma.bonmyd.backendincident.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponseGenericPagination<T> {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private int sliceTotalElements;
    private List<T> list;

}
