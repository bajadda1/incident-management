import {AfterViewInit, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {UtilisateurService} from '../../services/user-service/user.service';
import {data} from 'autoprefixer';
import {UserResponseDTO} from '../../models/user-response';
import {ApiResponseGenericPagination} from '../../models/api-response';
import {SectorDTO} from '../../models/sector';
import {SectorService} from '../../services/sector-service/sector.service';
import {AdminStatus} from '../../enums/admin-status';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.css',
  standalone: false
})

export class UserManagementComponent implements OnInit, AfterViewInit {

  professionals: UserResponseDTO[] = [];
  sectors: SectorDTO[] = [];

  //pagination
  totalElements = 0;
  currentPage: number = 0;
  pageSize: number = 2;
  totalPages!: number;
  sliceTotalElements = 0;
  pages: number[] = [];

  // Filter attributes
  filterEnabled: boolean | null = null;
  filterSectorId: number | null = null;
  filterUsername: string = '';
  filterFullname: string = '';

  enabledCases = [{case: "Enabled", val: true}, {case: "Disabled", val: false}]
  // Pagination options
  pageSizeOptions = [2, 4, 6, 8];


  //enable or disable professional
  professionalID: number | null = null;
  isEnabledProfessional: boolean | null = null;

  constructor(
    private userService: UtilisateurService,
    private cdr: ChangeDetectorRef,
    private sectorService: SectorService,
  ) {
  }

  ngOnInit(): void {
    this.getSectors();
    this.searchProfessionals();
  }

  ngAfterViewInit(): void {
    this.cdr.detectChanges();
  }

  searchProfessionals(): void {
    const filters = {
      enabled: this.filterEnabled,
      sectorId: this.filterSectorId,
      username: this.filterUsername,
      fullname: this.filterFullname,
    };

    console.log("filters:" + filters);
    console.log("current:" + this.currentPage);
    console.log("size:" + this.pageSize);

    this.userService.searchProfessionals(filters, this.currentPage, this.pageSize).subscribe(
      (response: ApiResponseGenericPagination<UserResponseDTO>) => {
        this.processResponse(response);
        console.log(response);
        console.log(this.professionals);

      },
      (error) => {
        console.error('Error fetching professionals:', error);
      }
    );
  }

  processResponse(response: ApiResponseGenericPagination<UserResponseDTO>): void {
    this.professionals = response.list;
    this.totalElements = response.totalElements;
    this.currentPage = response.currentPage;
    this.pageSize = response.pageSize;
    this.totalPages = response.totalPages;
    this.sliceTotalElements = response.sliceTotalElements;
    this.pages = Array.from({length: this.totalPages}, (_, i) => i);
  }

  handleFilterChange(): void {
    this.currentPage = 0; // Reset to the first page when filters change
    this.searchProfessionals();
  }

  resetFilters(): void {
    this.filterEnabled = null;
    this.filterSectorId = null;
    this.filterUsername = '';
    this.filterFullname = '';
    this.handleFilterChange();
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      console.log("before next" + this.currentPage)
      this.currentPage++;
      this.searchProfessionals();
      console.log("after next" + this.currentPage)
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      console.log("before prev" + this.currentPage)
      this.currentPage--;
      this.searchProfessionals();
      console.log("after prev" + this.currentPage)
      this.searchProfessionals();
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.searchProfessionals();
  }

  onPageSizeChange(newPageSize: number): void {
    this.pageSize = newPageSize;
    this.currentPage = 0; // Reset to the first page
    this.searchProfessionals();
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

  protected readonly AdminStatus = AdminStatus;


  enableProfessional(professionalId: number) {
    this.userService.enableUserById(professionalId).subscribe(
      (data) => {
        console.log(data)
      },
      (err) => {
        console.log(err);
      }
    )
  }

  disableProfessional(professionalId: number) {
    this.userService.disableUserById(professionalId).subscribe(
      (data) => {
        console.log(data)
      },
      (err) => {
        console.log(err);
      }
    )
  }

  handleEnableProfessional(professionalId: number, isEnabled: boolean) {
    this.professionalID = professionalId;
    this.isEnabledProfessional = isEnabled;
  }

  confirmEnableProfessional() {

    console.log(`Professional ID: ${this.professionalID}, Enabled: ${this.isEnabledProfessional}`);

    this.isEnabledProfessional ? this.enableProfessional(this.professionalID!) : this.disableProfessional(this.professionalID!);
  }

  dismissEnableProfessional(): void {
    // Reset the enabled state to its original value
    const professional = this.findProfessionalById(this.professionalID!);
    if (professional) {
      professional.enabled = !this.isEnabledProfessional;
    }
  }

  //find pro inside list on front
  private findProfessionalById(professionalId: number): any {
    return this.professionals.find((prof: any) => prof.id === professionalId);
  }
}
