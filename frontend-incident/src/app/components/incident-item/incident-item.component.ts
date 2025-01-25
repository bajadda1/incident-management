import {AfterViewInit, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {IncidentService} from '../../services/incident-service/incident.service';
import L from 'leaflet';
import "leaflet/dist/images/marker-shadow.png";
import "leaflet/dist/images/marker-icon-2x.png";
import "leaflet/dist/images/marker-icon.png";
import {UserService} from '../../services/auth-service/user.service';
import {IncidentDTO} from '../../models/incident';
import {Status} from '../../enums/status';
import {AdminStatus} from '../../enums/admin-status';
import {MapService} from '../../services/map-service/map-service.service';
import {RejectionDTO} from '../../models/rejection';
import {environment} from '../../../environments/environment';


// @ts-ignore
import {LocateControl} from "leaflet.locatecontrol";
import "leaflet.locatecontrol/dist/L.Control.Locate.min.css";
import {Lightbox} from 'ngx-lightbox';

@Component({
  selector: 'app-incident-item',
  templateUrl: './incident-item.component.html',
  styleUrl: './incident-item.component.css',
  standalone: false
})
export class IncidentItemComponent implements OnInit, AfterViewInit {

  locate = new LocateControl();
  imgBaseURL = environment.backendHost + environment.contextPath + "/";

  isAdmin!: boolean;
  incident!: IncidentDTO; // Store incident data
  incidentID!: number; // Store incident ID
  newStatus!: string;
  map: any;
  canBeUpdated!: boolean;
  statusesByRole = {
    admin: ['PUBLISHED', 'REJECTED'],
    professional: ['IN_PROGRESS', 'PROCESSED', 'BLOCKED']
  };

  // Define allowed transitions
  allowedTransitions: Record<Status, Status[]> = {
    [Status.DECLARED]: [Status.PUBLISHED, Status.REJECTED], // DECLARED -> PUBLISHED or REJECTED
    [Status.REJECTED]: [], // No transitions allowed
    [Status.PUBLISHED]: [Status.IN_PROGRESS], // PUBLISHED -> IN_PROGRESS
    [Status.IN_PROGRESS]: [Status.PROCESSED, Status.BLOCKED], // IN_PROGRESS -> PROCESSED or BLOCKED
    [Status.PROCESSED]: [], // Final state, no transitions allowed
    [Status.BLOCKED]: [] // Final state, no transitions allowed
  };

  professionalAcceptedStatus: Status[] = [];


  statusClasses = {
    DECLARED: 'bg-yellow-100 text-yellow-600',
    PUBLISHED: 'bg-green-100 text-green-600',
    REJECTED: 'bg-red-100 text-red-600',
    IN_PROGRESS: 'bg-blue-100 text-blue-600',
    PROCESSED: 'bg-purple-100 text-purple-600',
    BLOCKED: 'bg-gray-100 text-gray-600'
  };

  statuses = ['DECLARED', 'PUBLISHED', 'REJECTED', 'IN_PROGRESS', 'PROCESSED', 'BLOCKED'];
  availableStatuses: string[] = [];
  selectedStatus: any;

  // Operations on status
  chosenStatusForUpdating!: Status;

  protected readonly AdminStatus = AdminStatus;
  rejectionReason!: string;
  errorMsg = '';
  provinceLayer = L.layerGroup();
  regionsLayer = L.layerGroup();

  incidentLayer = L.layerGroup()

  constructor(
    private route: ActivatedRoute,
    private incidentService: IncidentService,
    private router: Router,
    protected authService: UserService,
    private cdr: ChangeDetectorRef,
    private mapService: MapService,
    private lightbox: Lightbox
  ) {
  }

  getAcceptedStatuses(currentStatus: Status) {
    this.professionalAcceptedStatus = this.allowedTransitions[currentStatus] || [];
  }


  ngOnInit(): void {

    this.isAdmin = this.authService.isAdmin;
    console.log("IS ADMIN" + this.isAdmin)
    // Fix paths for Leaflet icons
    L.Marker.prototype.options.icon = L.icon({
      iconUrl: 'assets/marker-icon.png',
      shadowUrl: 'assets/marker-shadow.png',
      shadowSize: [30, 65],
      iconAnchor: [12, 41],
      shadowAnchor: [7, 65]
    });

    // Get incident ID from route
    const incidentId = this.route.snapshot.paramMap.get('id');
    if (incidentId) {
      this.incidentID = parseInt(incidentId);
      this.loadIncidentData(this.incidentID); // Load incident data by ID
    }

    // Set allowed statuses based on user role
    this.availableStatuses = this.authService.isAdmin
      ? this.statusesByRole.admin
      : this.statusesByRole.professional;
  }

  ngAfterViewInit() {
  }

  private loadIncidentData(incidentId: number): void {
    this.incidentService.getIncidentsById(incidentId).subscribe(
      (data) => {
        this.incident = data;
        this.canUpdateStatus();
        this.selectedStatus = this.incident.status;
        this.incident.photo = this.imgBaseURL + this.incident.photo;
        this.getAcceptedStatuses(this.incident.status);
        console.log("incident photo:" + this.incident.photo)
        console.log("incident :" + this.incident)
        console.log("allowed status :" + this.professionalAcceptedStatus)
        // Initialize map after loading incident data
        this.initializeMap();
      },
      (err) => console.error('Error loading incident data:', err)
    );
  }

  private initializeMap(): void {
    if (this.map) {
      return; // Map already initialized, no need to do it again
    }
    if (!this.incident || !this.incident.location || !this.incident.provinceDTO.geom) {
      console.error('Incident location or province geometry data is missing.');
      return;
    }

    // Parse the point location
    const coordinates = this.mapService.parseWktPoint(this.incident.location);
    if (!coordinates) {
      console.error('Failed to parse incident location.');
      return;
    }

    const [lat, lng] = coordinates;

    // Initialize map and set center to incident location
    this.map = this.mapService.initializeMap('map', {
      center: [lat, lng], // Centering map on incident location
      zoom: 8,
    });

    // Add base layers
    const esriMapLayer = this.mapService.createTileLayer(
      'https://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/{z}/{y}/{x}',
      'Tiles &copy; Esri &mdash; GIS User Community'
    );

    const satelliteLayer = this.mapService.createTileLayer(
      'https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/{z}/{x}/{y}{r}.png',
      '© Stamen Design, under CC BY 3.0. Map data © OpenStreetMap contributors'
    );

    // Add layers control
    this.mapService.addLayersControl(
      this.map,
      {
        'Esri Map Layer': esriMapLayer,
        Satellite: satelliteLayer,
      },
      {
        regions: this.regionsLayer,
        Provinces: this.provinceLayer,
        Incident: this.incidentLayer
      },
      esriMapLayer
    );


    // Parse and add province polygon to the map
    const regionCoordinates = this.mapService.parseCoordinates(this.incident.provinceDTO.regionDTO.geom);
    if (regionCoordinates) {
      const polygon = L.polygon(regionCoordinates, {
        color: 'blue',
        weight: 2,
        fillOpacity: 0.3,
      }).addTo(this.regionsLayer);

      polygon.bindPopup(`<b>${this.incident.provinceDTO.regionDTO.name}</b><br>Area: ${this.incident.provinceDTO.regionDTO.area}`);

      polygon.on('click', () => {
        polygon.setStyle({
          color: 'red',
          weight: 3,
          fillOpacity: 0.5,
        });
      });

      this.regionsLayer.addTo(this.map);
    }


    // Parse and add province polygon to the map
    const provinceCoordinates = this.mapService.parseCoordinates(this.incident.provinceDTO.geom);
    if (provinceCoordinates) {
      const polygon = L.polygon(provinceCoordinates, {
        color: 'yellow',
        weight: 2,
        fillOpacity: 0.3,
      }).addTo(this.provinceLayer);

      polygon.bindPopup(`<b>${this.incident.provinceDTO.name}</b><br>Area: ${this.incident.provinceDTO.area}`);

      polygon.on('click', () => {
        polygon.setStyle({
          color: 'green',
          weight: 3,
          fillOpacity: 0.5,
        });
      });

      this.provinceLayer.addTo(this.map);
    }

    // Add other map controls
    this.mapService.addScaleControl(this.map);
    this.mapService.addGeocoder(this.map); // Enable geocoder for search

    // Add a marker at the incident location
    const incidentMarker = L.marker([lat, lng], {
      title: 'Incident Location',
    }).addTo(this.incidentLayer);

    incidentMarker.bindPopup(`<b>Incident Location</b><br>Latitude: ${lat}<br>Longitude: ${lng}"`).openPopup();

    // Add Locate Control
    const locateControl = new LocateControl();
    locateControl.addTo(this.map);
  }


  // canUpdateStatus(): void {
  //   this.canBeUpdated = this.authService.isAdmin
  //     ? this.incident.status === Status.DECLARED
  //     : this.incident.status !== Status.DECLARED && this.incident.status !== Status.REJECTED;
  // }

  // Method to check if the status can be updated
  canUpdateStatus(): boolean {
    // newStatus: Status
    // // Check if the role allows the new status
    // const roleAllowedStatuses = this.availableStatuses;
    // // Check if the new status is allowed for the current old status
    // const validNextStatuses = this.allowedTransitions[this.incident.status] || [];
    // this.canBeUpdated = validNextStatuses.includes(newStatus);
    // return validNextStatuses.includes(newStatus);
    this.canBeUpdated = this.authService.isAdmin ? this.incident.status == Status.DECLARED  : this.incident.status == Status.PUBLISHED || this.incident.status == Status.IN_PROGRESS;

    console.log("CAN BE UP:"+this.canBeUpdated)
    return this.canBeUpdated;
  }

  updateIncidentStatus(): void {
    this.incidentService.updateIncidentStatus(this.incidentID, this.selectedStatus).subscribe({
      next: (response) => {
        this.loadIncidentData(this.incidentID);
        this.cdr.detectChanges();
        this.incident = response;
        this.errorMsg = '';
        console.log(response);
      },
      error: (err) => {
        this.loadIncidentData(this.incidentID);
        this.cdr.detectChanges();
        this.errorMsg = err.error.message;
      }
    });
  }

  rejectIncident(incidentId: number): void {
    this.errorMsg = '';
    const rejectionDTO: RejectionDTO = {
      id: null,
      reason: this.rejectionReason,
      date: new Date() // Current date
    };

    this.incidentService.rejectIncident(incidentId, rejectionDTO).subscribe(
      (response) => {
        this.incident = response
        console.log('Incident rejected successfully:', response);
        this.errorMsg = '';
        // Handle the successful response here
        this.canUpdateStatus();
        this.cdr.detectChanges();
        window.location.reload();
      },
      (err) => {
        console.error('Error rejecting incident:', err);
        // Handle the error response here
        this.errorMsg = err.error.message;

        this.cdr.detectChanges();
      }
    );
  }

  showSelectedStatus(): void {
    console.log(this.selectedStatus);
  }

  updateStatusOperation(): void {
    this.errorMsg = '';
    this.selectedStatus = this.chosenStatusForUpdating;
    this.updateIncidentStatus();
  }

  rejectOperation(): void {
    this.errorMsg = '';
    if (this.rejectionReason.trim() == '') {
      return;
    }
    this.selectedStatus = this.chosenStatusForUpdating;
    this.rejectIncident(this.incidentID);
  }


  confirmUpdateStatus() {
    this.errorMsg = '';
    if (this.chosenStatusForUpdating != Status.REJECTED) {
      this.updateStatusOperation();
    } else {
      this.rejectOperation();
    }
  }

  protected readonly Status = Status;

  openLightbox(): void {
    const album = [
      {src: this.incident.photo, caption: 'Incident Photo', thumb: this.incident.photo}
    ];
    this.lightbox.open(album, 0);
  }

  closeLightbox(): void {
    this.lightbox.close();
  }

}
