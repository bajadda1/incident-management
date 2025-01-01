import {AfterViewInit, Component} from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-fullscreen';
import 'leaflet.locatecontrol';
import 'leaflet-control-geocoder';
import {RegionsService} from '../../services/territoriale-service/regions.service';
import {ProvincesService} from '../../services/territoriale-service/provinces.service';
import {MapService} from '../../services/map-service/map-service.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css'],
  standalone: false
})
export class MapComponent implements AfterViewInit {
  private regionsLayer = L.layerGroup();
  private provincesLayer = L.layerGroup();
  private markersLayer = L.layerGroup();

  constructor(
    private regionsService: RegionsService,
    private provincesService: ProvincesService,
    private mapService: MapService
  ) {
  }

  ngAfterViewInit(): void {
    const map = this.mapService.initializeMap('map', {
      center: [51.505, -0.09],
      zoom: 13,
    });


    const esriMapLayer = this.mapService.createTileLayer(
      'https://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/{z}/{y}/{x}',
      'Tiles &copy; Esri &mdash; GIS User Community'
    );


    const satelliteLayer = this.mapService.createTileLayer(
      'https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/{z}/{x}/{y}{r}.png',
      '© Stamen Design, under CC BY 3.0. Map data © OpenStreetMap contributors'
    );

    // Set the default tile layer (e.g., Esri Map Layer)
    this.mapService.addLayersControl(
      map,
      {
        'Esri Map Layer': esriMapLayer,
        Satellite: satelliteLayer,
      },
      {
        Regions: this.regionsLayer,
        Provinces: this.provincesLayer,
      },
      //default base layer
      esriMapLayer
    );

    this.mapService.addScaleControl(map);
    this.mapService.locateUser(map); // Enable geolocation
    this.mapService.addGeocoder(map); // Enable geocoder for search
    this.loadRegions();
    this.loadProvinces();
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
        const polygon = this.mapService.addPolygon(this.mapService.getMap(), province.geom, {
            color: 'yellow',
            weight: 2,
            fillOpacity: 0.5
          }, this.provincesLayer
        );
        polygon.bindPopup(`<b>${province.name}</b><br>Area: ${province.area}`);
      });
    });
  }

}
