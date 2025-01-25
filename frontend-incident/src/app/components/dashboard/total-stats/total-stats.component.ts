import {Component, OnInit} from '@angular/core';
import {TotalStats} from '../../../models/total-stats';
import {TotalStatsService} from '../../../services/dashboard-service/total-stats.service';
import {data} from 'autoprefixer';

@Component({
  selector: 'app-total-stats',
  standalone: false,

  templateUrl: './total-stats.component.html',
  styleUrl: './total-stats.component.css'
})
export class TotalStatsComponent implements OnInit {

  constructor(private totalStatsService: TotalStatsService) {
  }

  totalStats!: TotalStats;

  ngOnInit(): void {
    this.getTotalStats()
  }

  getTotalStats() {
    this.totalStatsService.getTotalStats().subscribe(
      (data) => {
        this.totalStats = data;
      }, (err) => {
        console.log(err)
      }
    )
  }


}
