import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {ChartConfiguration, ChartData, ChartType} from 'chart.js';
import {TotalStatsService} from '../../../services/dashboard-service/total-stats.service';

import {Status} from '../../../enums/status';
import {BaseChartDirective} from 'ng2-charts';

@Component({
  selector: 'app-pie-chart',
  standalone: false,

  templateUrl: './pie-chart.component.html',
  styleUrl: './pie-chart.component.css'
})
export class PieChartComponent implements OnInit {

  @ViewChild(BaseChartDirective) actualChart: BaseChartDirective | undefined;

  constructor(private totalStatsService: TotalStatsService,
              private cdr: ChangeDetectorRef) {
  }


  //===========Filter attributes

  selectedRegion = null;
  selectedSector: number | null = null;
  selectedType = null;

  selectedStartDate: Date | null = null;
  selectedEndDate: Date | null = null;

  // Extract all statuses from the Status enum
  private readonly allStatuses: string[] = Object.values(Status);

  // Pie chart configuration
  public pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      tooltip: {
        enabled: true,
      },
    },
  };

// Pie chart type
  public pieChartType: ChartType = 'pie';

  // Pie chart data
  public pieChartData: ChartData<'pie'> = {
    labels: [], // Labels for each slice
    datasets: [
      {
        data: [], // Values for each slice
        backgroundColor: [
          'rgba(47,241,4,0.5)',  // Color for first slice
          'rgba(54, 162, 235, 0.5)', // Color for second slice
          'rgba(255, 206, 86, 0.5)', // Color for third slice
          'rgba(75, 192, 192, 0.5)', // Color for fourth slice
          'rgba(153, 102, 255, 0.5)', // Color for fifth slice
          'rgb(84,102,145, 0.5)', // Color for six slice
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)',
          'rgb(84,102,145,1)', // Color for six slice
        ],
        borderWidth: 1,
      },
    ],
  };

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


        // Update Pie Chart
        this.pieChartData.labels = this.allStatuses;
        this.pieChartData.datasets[0].data = this.allStatuses.map(
          (status) => statusCounts[status] || 0
        );

        // Trigger Change Detection
        this.cdr.markForCheck();
        this.cdr.detectChanges();

        if (this.actualChart) {
          this.actualChart.update();
        }

      }, (err) => {
        console.log(err)
      })
  }

  ngOnInit(): void {


    this.fetchGroupedByStatus();
  }

}
