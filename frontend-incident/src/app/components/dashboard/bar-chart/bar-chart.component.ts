import {AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {ChartConfiguration, ChartData, ChartType} from 'chart.js';
import {TotalStatsService} from '../../../services/dashboard-service/total-stats.service';

import {BaseChartDirective} from 'ng2-charts';
import {Status} from '../../../enums/status';
import {SectorService} from '../../../services/sector-service/sector.service';
import {RegionsService} from '../../../services/territoriale-service/regions.service';
import {ProvincesService} from '../../../services/territoriale-service/provinces.service';
import {TypeService} from '../../../services/type-service/type.service';
import {RegionDTO} from '../../../models/region';
import {SectorDTO} from '../../../models/sector';
import {TypeDTO} from '../../../models/type';


@Component({
  selector: 'app-bar-chart',
  standalone: false,

  templateUrl: './bar-chart.component.html',
  styleUrl: './bar-chart.component.css'
})
export class BarChartComponent implements OnInit, AfterViewInit {

  @ViewChild('barChart') barChart: BaseChartDirective | undefined;
  @ViewChild(BaseChartDirective) actualChart: BaseChartDirective | undefined;

  constructor(private totalStatsService: TotalStatsService,
              private sectorService: SectorService,
              private regionsService: RegionsService,
              private typeService: TypeService,
              private cdr: ChangeDetectorRef) {
  }

  //===========Filter attributes

  selectedRegion = null;
  selectedSector: number | null = null;
  selectedType = null;

  selectedStartDate: Date | null = null;
  selectedEndDate: Date | null = null;
  regions: RegionDTO[] = [];
  sectors: SectorDTO[] = [];
  types: TypeDTO[] = [];
  filteredTypes: TypeDTO[] = [];

  // Extract all statuses from the Status enum
  private readonly allStatuses: string[] = Object.values(Status);


  // Bar chart configuration
  public barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top'
      },
      tooltip: {
        enabled: true
      }
    },
    scales: {
      x: {},
      y: {
        beginAtZero: true
      }
    }
  };

  public barChartType: ChartType = 'bar';

  // Define the proper type for chart data
  public barChartData: ChartData<'bar'> = {
    labels: [], // Labels for the x-axis
    datasets: [
      {
        data: [], // Counts for each status
        label: 'Incident Counts',
        backgroundColor: 'rgba(54, 162, 235, 0.5)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 2
      }
    ]
  };

  ngOnInit(): void {
    this.fetchGroupedByStatus();
    this.getRegions();
    this.getTypes();
    this.getSectors();
  }

  fetchGroupedByStatus(): void {
    const filters = {
      regionId: this.selectedRegion,
      sectorId: this.selectedSector,
      typeId: this.selectedType,
      startDate: this.selectedStartDate,
      endDate: this.selectedEndDate,
    };

    this.totalStatsService.gettGroupedByStatus(filters).subscribe(
      (data) => {

        console.log(data)
// Convert API data to a dictionary for easy lookup
        const statusCounts = data.reduce((acc, item) => {
          acc[item.status] = item.count;
          return acc;
        }, {} as Record<string, number>);

        // Ensure all statuses are represented, using 0 for missing statuses
        this.barChartData.labels = this.allStatuses;
        this.barChartData.datasets[0].data = this.allStatuses.map(
          (status) => statusCounts[status] || 0
        );


        // Trigger Change Detection
        this.cdr.markForCheck();
        this.cdr.detectChanges();

        if (this.actualChart) {
          this.actualChart.update();
        }

        // Update Bar Chart
        if (this.barChart) {
          this.barChart.update();
        }


      }, (err) => {
        console.log(err)
      })
  }


  getRegions() {
    this.regionsService.getRegions().subscribe(
      (data) => {
        this.regions = data;

      },
      error => {
        console.log(error)
      }
    )
  }

  getTypes() {
    this.typeService.getTypes().subscribe(
      (data) => {
        this.types = data;
        this.filteredTypes = data;
      },
      error => {
        console.log(error)
      }
    )
  }

  getSectors() {
    this.sectorService.getSectors().subscribe(
      (data) => {
        this.sectors = data;
      },
      error => {
        console.log(error)
      }
    )
  }

  // Handle sector selection
  onSectorChange(): void {
    // Filter types based on selected sector
    if (this.selectedSector) {
      this.selectedType = null;
      this.filteredTypes = this.types.filter(
        (type) => type.sectorDTO?.id === this.selectedSector
      );
    } else {
      this.filteredTypes = this.types;
    }

    this.fetchGroupedByStatus();
  }

  resetFilters(): void {

    this.selectedRegion = null; // Reset region dropdown
    this.selectedSector = null; // Reset sector dropdown
    this.selectedType = null; // Reset type dropdown
    this.selectedStartDate = null;
    this.selectedEndDate = null;
    // Reinitialize filters (if needed)
    this.fetchGroupedByStatus();
  }

  ngAfterViewInit(): void {
    this.cdr.detectChanges();
  }
}
