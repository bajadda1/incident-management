import {AfterViewChecked, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Dropdown, initTWE, Ripple} from 'tw-elements';

@Component({
  selector: 'app-incident-list',
  templateUrl: './incident-list.component.html',
  styleUrl: './incident-list.component.css'
})
export class IncidentListComponent implements OnInit,AfterViewChecked{

  constructor(private cdr: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.cdr.detectChanges();
  }
  ngAfterViewChecked(): void {
    this.cdr.detectChanges();
  }
  incidents = [
    {
      id: 1,
      photo: 'https://via.placeholder.com/150',
      createdAt: new Date('2024-11-01'),
      updatedAt: new Date('2024-11-20'),
      description: 'Broken streetlight',
      status: 'OPEN',
      typeDTO: {name: 'Electrical Issue'},
      sectorDTO: {name: 'Urban Area'},
      location: '34.0522, -118.2437',
    },
    {
      id: 2,
      photo: 'https://via.placeholder.com/150',
      createdAt: new Date('2024-11-05'),
      updatedAt: new Date('2024-11-22'),
      description: 'Water leakage',
      status: 'RESOLVED',
      typeDTO: {name: 'Plumbing'},
      sectorDTO: {name: 'Residential Zone'},
      location: '40.7128, -74.0060',
    },
  ];


  statuses = ['All','Open', 'Closed', 'In Progress', 'Resolved', 'On Hold'];
  selectedStatus = 'Open';
  filteredIncidents: any[] = this.incidents;


  filterByStatus(status: string): void {
    if (status.toUpperCase()=='ALL'){
      this.filteredIncidents = this.incidents;
    }
    else {
      this.selectedStatus = status.toUpperCase();
      this.filteredIncidents = this.incidents.filter(
        (incident) => {
          console.log(this.selectedStatus)
          return incident.status === this.selectedStatus;
        }
      );
    }

    console.log(this.filteredIncidents);
    this.cdr.detectChanges(); // Manually trigger change detection

  }

}
