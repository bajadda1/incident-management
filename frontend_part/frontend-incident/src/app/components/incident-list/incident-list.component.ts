import {AfterViewInit, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {IncidentService} from '../../services/incident-service/incident.service';
import {IncidentDTO} from '../../models/incident';
import {Router} from '@angular/router';
import {ApiResponseGenericPagination} from '../../models/api-response';
import {UserService} from '../../services/auth-service/user.service';
import {UtilisateurService} from '../../services/user-service/user.service';
import {SectorDTO} from '../../models/sector';
import {SectorService} from '../../services/sector-service/sector.service';
import {RegionsService} from '../../services/territoriale-service/regions.service';
import {ProvincesService} from '../../services/territoriale-service/provinces.service';
import {RegionDTO} from '../../models/region';
import {ProvinceDTO} from '../../models/province';
import {TypeService} from '../../services/type-service/type.service';
import {TypeDTO} from '../../models/type';

@Component({
  selector: 'app-incident-list',
  templateUrl: './incident-list.component.html',
  styleUrls: ['./incident-list.component.css'],
  standalone: false
})
export class IncidentListComponent implements OnInit, AfterViewInit {

  incidents: IncidentDTO[] = [];
  sectors: SectorDTO[] = [];
  filteredIncidents: IncidentDTO[] = [];
  statuses = {
    admin: ['ALL', 'PUBLISHED', 'REJECTED'],
    professional: ['ALL', 'IN_PROGRESS', 'PROCESSED', 'BLOCKED']
  };

  statusesByRole = {
    admin: ['PUBLISHED', 'REJECTED'],
    professional: ['IN_PROGRESS', 'PROCESSED', 'BLOCKED']
  };

  availableStatuses: string[] = [];
  filterStatuses: string[] = ['', 'DECLARED', 'IN_PROGRESS', 'PUBLISHED', 'REJECTED', 'PROCESSED', 'BLOCKED'];

  //===========Filter attributes
  selectedStatus: string = ''; // Default filter
  selectedProvince = null;
  selectedRegion = null;
  selectedSector: number | null = null;
  selectedType = null;
  selectedDate = '';
  searchTerm = '';

  //========Pagination properties
  totalElements = 0;
  currentPage: number = 0;
  pageSize: number = 5;
  totalPages!: number;
  totalItems!: number;
  pages: number[] = [];
  //pagination scale
  options = [5, 10, 20, 40]
  //if Logged in as pro => get sector
  proSector!: SectorDTO;

  regions: RegionDTO[] = []
  provinces: ProvinceDTO[] = []
  types: TypeDTO[] = [];

  filteredTypes: TypeDTO[] = [];
  filteredSectors: SectorDTO[] = [];

  constructor(private incidentService: IncidentService,
              private router: Router,
              protected authService: UserService,
              private userService: UtilisateurService,
              private sectorService: SectorService,
              private regionsService: RegionsService,
              private provincesService: ProvincesService,
              private typeService: TypeService,
              private cdr: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.getRegions();
    this.getTypes();
    // Set allowed statuses based on user role
    if (this.authService.isAdmin) {
      this.availableStatuses = this.statusesByRole.admin;
      this.getSectors();
      this.searchIncidents();

    } else {
      this.filterStatuses = ['', ...this.statusesByRole.professional];
      this.availableStatuses = this.statusesByRole.professional;
      this.getCurrentUser().then(() => this.searchIncidentsByProfessional());
    }
  }

  ngAfterViewInit(): void {
  }

  searchIncidents(): void {

    const filters = {
      status: this.selectedStatus,
      provinceId: this.selectedProvince,
      regionId: this.selectedRegion,
      sectorId: this.selectedSector,
      typeId: this.selectedType,
      description: this.searchTerm,
      date: this.selectedDate,
    };

    this.incidentService.searchIncidents(filters,
      this.currentPage, this.pageSize).subscribe(data => {
      this.processIncidentResponse(data); // Adapt as per your API response
      console.log(data)
    });
  }

  searchIncidentsByProfessional(): void {
    this.incidentService.searchIncidentsByProfessional({
      status: this.selectedStatus,
      provinceId: this.selectedProvince,
      regionId: this.selectedRegion,
      sectorId: this.selectedSector,
      typeId: this.selectedType,
      description: this.searchTerm,
      date: this.selectedDate
    }, this.currentPage, this.pageSize).subscribe(data => {
      this.processIncidentResponse(data); // Adapt as per your API response
    });
  }

  processIncidentResponse(response: ApiResponseGenericPagination<IncidentDTO>): void {
    this.incidents = response.list;
    this.currentPage = response.currentPage;
    this.pageSize = response.pageSize;
    this.totalPages = response.totalPages;
    this.totalElements = response.totalElements;
    this.totalItems = response.list.length;
    this.pages = Array.from({length: this.totalPages}, (_, i) => i);
  }

  handleFilterIncidents() {
    this.currentPage = 0;
    this.authService.isAdmin ? this.searchIncidents() : this.searchIncidentsByProfessional();
  }

  changePageHandleIncidents() {
    this.authService.isAdmin ? this.searchIncidents() : this.searchIncidentsByProfessional();
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.changePageHandleIncidents()
    }
  }

  prevPage(): void {
    if (this.currentPage >= 1) {
      this.currentPage--;
      this.changePageHandleIncidents()
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.changePageHandleIncidents()
    this.cdr.detectChanges();
  }


  getStatusClass(status: string): string {
    const statusClasses = {
      DECLARED: 'bg-yellow-100 text-yellow-600',
      PUBLISHED: 'bg-green-100 text-green-600',
      REJECTED: 'bg-red-100 text-red-600',
      IN_PROGRESS: 'bg-blue-100 text-blue-600',
      PROCESSED: 'bg-purple-100 text-purple-600',
      BLOCKED: 'bg-gray-100 text-gray-600'
    };
    // @ts-ignore
    return statusClasses[status] || 'bg-gray-50 text-gray-800';
  }


  protected readonlyMath = Math;


  onPageSizeChange(newPageSize: number): void {
    this.pageSize = newPageSize;
    this.currentPage = 0; // Reset to the first page
    this.handleFilterIncidents();
  }

  getCurrentUser(): Promise<void> {
    if (this.authService.isAdmin)
      return Promise.resolve();

    return new Promise((resolve, reject) => {
      this.userService.getCurrentUser().subscribe(
        (res) => {
          this.proSector = res.sectorDTO;
          this.selectedSector = this.proSector.id;
          resolve();
        },
        (err) => {
          console.error(err);
          reject(err);
        }
      );
    });
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
        this.filteredSectors = data;
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

    this.handleFilterIncidents();
  }

  resetFilters(): void {
    this.searchTerm = ''; // Clear search input
    this.selectedStatus = ''; // Reset status dropdown
    this.selectedRegion = null; // Reset region dropdown
    this.selectedSector = null; // Reset sector dropdown
    this.selectedType = null; // Reset type dropdown

    // Reinitialize filters (if needed)
    this.handleFilterIncidents();
  }

  protected readonly Math = Math;
}
