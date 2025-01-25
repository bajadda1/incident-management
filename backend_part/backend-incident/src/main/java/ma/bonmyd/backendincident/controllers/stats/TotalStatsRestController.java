package ma.bonmyd.backendincident.controllers.stats;

import lombok.RequiredArgsConstructor;
import ma.bonmyd.backendincident.dtos.ApiResponseGenericPagination;
import ma.bonmyd.backendincident.dtos.incident.IncidentDTO;
import ma.bonmyd.backendincident.dtos.incident.IncidentStatusGroupDTO;
import ma.bonmyd.backendincident.dtos.stats.TotalStats;
import ma.bonmyd.backendincident.enums.Status;
import ma.bonmyd.backendincident.services.stats.impl.TotalStatsServiceImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("${stats.api}")
@RequiredArgsConstructor
public class TotalStatsRestController {

    private final TotalStatsServiceImpl totalStatsService;

    @GetMapping
    public TotalStats getAllTotalStats() {
        return this.totalStatsService.getTotalStats();
    }

    @GetMapping("/grouped-by-status")
    public List<IncidentStatusGroupDTO> getIncidentsGroupedByStatus() {
        return this.totalStatsService.getIncidentsGroupedByStatus();
    }

    @GetMapping("/search/grouped-by-status")
    public List<IncidentStatusGroupDTO> getGroupedByStatus(@RequestParam(name = "sector", required = false) Long sectorId,
                                                           @RequestParam(name = "region", required = false) Long regionId,
                                                           @RequestParam(name = "type", required = false) Long typeId,
                                                           @RequestParam(name = "start-date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                                           @RequestParam(name = "end-date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        return totalStatsService.getGroupedByStatus(sectorId, regionId, typeId, startDate, endDate);
    }
}
