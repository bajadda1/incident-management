import {AfterViewInit, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-fullscreen';
import 'leaflet.locatecontrol';
import 'leaflet-control-geocoder';
import {RegionsService} from '../../services/territoriale-service/regions.service';
import {ProvincesService} from '../../services/territoriale-service/provinces.service';
import {MapService} from '../../services/map-service/map-service.service';
import 'leaflet.markercluster';
// @ts-ignore
import {LocateControl} from "leaflet.locatecontrol";
import "leaflet.locatecontrol/dist/L.Control.Locate.min.css";
import {IncidentService} from '../../services/incident-service/incident.service';
import {IncidentDTO} from '../../models/incident';
import {RegionDTO} from '../../models/region';
import {SectorDTO} from '../../models/sector';
import {TypeDTO} from '../../models/type';
import {SectorService} from '../../services/sector-service/sector.service';
import {TypeService} from '../../services/type-service/type.service';
import {Status} from '../../enums/status';


@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css'],
  standalone: false
})
export class MapComponent implements AfterViewInit, OnInit {


  //===========Filter attributes
  selectedStatus: string | null = null; // Default filter
  selectedRegion = null;
  selectedSector: number | null = null;
  selectedType = null;
  searchTerm = '';
  selectedStartDate: Date | null = null;
  selectedEndDate: Date | null = null;
  regions: RegionDTO[] = [];
  sectors: SectorDTO[] = [];
  types: TypeDTO[] = [];
  filteredTypes: TypeDTO[] = [];
  filteredSectors: SectorDTO[] = [];

  // Extract all statuses from the Status enum
  allStatuses: string[] = Object.values(Status);
  private regionsLayer = L.layerGroup();
  private provincesLayer = L.layerGroup();
  // @ts-ignore
  // @ts-ignore
  private incidentsClusterLayer = L.markerClusterGroup({
    iconCreateFunction: (cluster: { getChildCount: () => any; }) => {
      const count = cluster.getChildCount(); // Get the number of markers in the cluster
      let size = 'small';

      // Determine cluster size based on marker count
      if (count >= 10 && count < 50) {
        size = 'medium';
      } else if (count >= 50) {
        size = 'large';
      }

      return L.divIcon({
        html: `<div class="cluster-icon w-10 h-10 rounded-full text-xl font-bold bg-green-300 grid place-items-center"><span class="">${count}</span></div>`,
        className: 'custom-cluster-icon', // Custom class for styling
        iconSize: L.point(40, 40, true), // Adjust size if needed
      });
    },

  });


  constructor(
    private incidentsService: IncidentService, // Add service for incidents
    private mapService: MapService,
    private sectorService: SectorService,
    private regionsService: RegionsService,
    private provincesService: ProvincesService,
    private typeService: TypeService,
    private cdr: ChangeDetectorRef
  ) {
  }

  ngAfterViewInit(): void {

    // Fix paths for Leaflet icons
    // L.Marker.prototype.options.icon = L.icon({
    //   iconUrl: 'assets/marker-icon.png',
    //   shadowUrl: 'assets/marker-shadow.png',
    //   shadowSize: [30, 65],
    //   iconAnchor: [12, 41],
    //   shadowAnchor: [7, 65]
    // });

    // Create a legend control
    // @ts-ignore
    const legend = L.control({position: 'bottomright'});

    legend.onAdd = function () {
      const div = L.DomUtil.create('div', 'info legend');

      // Legend HTML content
      div.innerHTML = `
      <h4>Incident Status</h4>
      <div><i style="background: yellow; width: 12px; height: 12px; display: inline-block;"></i> DECLARED</div>
      <div><i style="background: green; width: 12px; height: 12px; display: inline-block;"></i> PUBLISHED</div>
      <div><i style="background: red; width: 12px; height: 12px; display: inline-block;"></i> REJECTED</div>
      <div><i style="background: blue; width: 12px; height: 12px; display: inline-block;"></i> IN_PROGRESS</div>
      <div><i style="background: purple; width: 12px; height: 12px; display: inline-block;"></i> PROCESSED</div>
      <div><i style="background: gray; width: 12px; height: 12px; display: inline-block;"></i> BLOCKED</div>
    `;

      return div;
    };

    legend.addTo(this.mapService.getMap()!);


  }

  private loadRegions(): void {
    this.regionsService.getRegions().subscribe((regions) => {
      regions.forEach((region: any) => {
        const polygon = this.mapService.addPolygon(
          this.mapService.getMap(),
          region.geom,
          {color: 'blue', weight: 2, fillOpacity: 0.5},
          this.regionsLayer
        );
        polygon.bindPopup(`<b>${region.name}</b><br>Area: ${region.area}`);
      });
    });
  }

  private loadProvinces(): void {
    this.provincesService.getProvinces().subscribe((provinces) => {
      provinces.forEach((province: any) => {
        const polygon = this.mapService.addPolygon(
          this.mapService.getMap(),
          province.geom,
          {color: 'yellow', weight: 2, fillOpacity: 0.5},
          this.provincesLayer
        );
        polygon.bindPopup(`<b>${province.name}</b><br>Area: ${province.area}`);
      });
    });
  }


  loadIncidents(): void {
    // Clear existing markers from the cluster layer
    this.incidentsClusterLayer.clearLayers();
    this.incidentsService.getIncidents({
      startDate: this.selectedStartDate,
      endDate: this.selectedEndDate,
      sectorId: this.selectedSector,
      regionId: this.selectedRegion,
      description: this.searchTerm,
      typeId: this.selectedType,
      provinceId: null,
      status: this.selectedStatus,
    }).subscribe((response) => {
      response.forEach((incident: IncidentDTO) => {
        const coordinates = this.mapService.parseWktPoint(incident.location); // Parse WKT geometry
        if (coordinates) {
          const iconClass = this.getIconClassByStatus(incident.status); // Get icon class based on status

          const marker = L.marker(coordinates, {
            icon: L.divIcon({
              className: `custom-marker`, // Add a general class for marker styling
              html: `<i class="${iconClass}"></i>`, // Include the <i> element dynamically
              iconSize: [30, 30], // Adjust size as needed
              iconAnchor: [15, 30], // Adjust anchor as needed
            }),
          }).bindPopup(`
          Description: ${incident.description}<br>
          Type: ${incident.typeDTO.name}<br>
          Status: ${incident.status}
        `);

          // Add marker to the cluster layer

          this.incidentsClusterLayer.addLayer(marker);
        }
      });

      // Add cluster layer to map
      this.incidentsClusterLayer.addTo(this.mapService.getMap()!);
    });
  }

  goToIncident() {
    console.log("Clicked")
  }


  getIconClassByStatus(status: string): string {
    const iconClasses = {
      DECLARED: 'fa-solid fa-location-dot text-3xl font-bold shadow-lg text-yellow-500', // Example: Font Awesome yellow icon
      PUBLISHED: 'fa-solid fa-location-dot text-3xl font-bold shadow-lg text-green-500',       // Example: Font Awesome green icon
      REJECTED: 'fa-solid fa-location-dot text-3xl font-bold text-red-500',          // Example: Font Awesome red icon
      IN_PROGRESS: 'fa-solid fa-location-dot text-3xl font-bold text-blue-500',    // Example: Font Awesome blue icon
      PROCESSED: 'fa-solid fa-location-dot text-3xl font-bold text-purple-500',              // Example: Font Awesome purple icon
      BLOCKED: 'fa-solid fa-location-dot text-3xl font-bold text-gray-500',                   // Example: Font Awesome gray icon
    };
    // @ts-ignore
    return iconClasses[status] || 'fa-solid fa-location-dot text-3xl font-bold text-gray-400'; // Default icon
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

    this.loadIncidents();
  }

  resetFilters(): void {
    this.searchTerm = ''; // Clear search input
    this.selectedStatus = null; // Reset status dropdown
    this.selectedRegion = null; // Reset region dropdown
    this.selectedSector = null; // Reset sector dropdown
    this.selectedType = null; // Reset type dropdown

    // Reinitialize filters (if needed)
    this.loadIncidents();
  }

  ngOnInit(): void {

    const map = this.mapService.initializeMap('map', {
      center: [33.58, -7.60],
      zoom: 5,
    });

    const esriMapLayer = this.mapService.createTileLayer(
      'https://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/{z}/{y}/{x}',
      'Tiles &copy; Esri &mdash; GIS User Community'
    );

    esriMapLayer.addTo(map);

    this.mapService.addLayersControl(
      map,
      {
        'Esri Map Layer': esriMapLayer,
      },
      {
        Regions: this.regionsLayer,
        Provinces: this.provincesLayer,
        Incidents: this.incidentsClusterLayer, // Add cluster layer to overlays

      },
      esriMapLayer
    );

    // Add Locate Control
    const locateControl = new LocateControl();
    locateControl.addTo(map);

    this.mapService.addScaleControl(map);
    this.mapService.addGeocoder(map);

    this.loadRegions();
    this.loadProvinces();
    this.loadIncidents();


    this.getRegions()
    this.getTypes()
    this.getSectors()
  }


}
