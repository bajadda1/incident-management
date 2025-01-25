package ma.bonmyd.backendincident.dtos.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TotalStats {

    private Long totalCitizens;
    private Long totalDeclaredIncidents;
    private Long totalProcessedIncidents;
    private Long totalSectors;

}
