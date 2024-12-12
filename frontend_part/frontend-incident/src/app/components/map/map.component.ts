import {AfterViewInit, Component} from '@angular/core';
import * as L from 'leaflet';
import {RegionsService} from '../../services/territoriale-service/regions.service';
import {Observable} from 'rxjs';
import 'leaflet-fullscreen';
import 'leaflet.locatecontrol';
import 'leaflet-control-geocoder';
import {ProvincesService} from '../../services/territoriale-service/provinces.service'; // Import the LocateControl plugin

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements AfterViewInit {
  map: any;
  regionsLayer = L.layerGroup();
  provincesLayer = L.layerGroup();
  markersLayer = L.layerGroup();
  private lastMarker: L.Marker | null = null;
  private lastClickedPolygon: L.Polygon | null = null;
  private lastClickedPolygonProvince: L.Polygon | null = null;

  constructor(private regionsService: RegionsService,
              private provincesService: ProvincesService) {
  }

  ngAfterViewInit(): void {
    this.initializeMap();
    this.loadRegions();
    this.loadProvinces();
    this.addEventListeners();
  }

  // Initialize the map
  private initializeMap(): void {
    // Define base layers
    const openStreetMapLayer = this.createTileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
      '© OpenStreetMap contributors'
    );


    const satelliteLayer = this.createTileLayer(
      'https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/{z}/{x}/{y}{r}.png',
      '© Stamen Design, under CC BY 3.0. Map data © OpenStreetMap contributors'
    );

    const Esri_WorldTopoMap = this.createTileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/{z}/{y}/{x}',
      'Tiles &copy; Esri &mdash; Esri, DeLorme, NAVTEQ, TomTom, Intermap, iPC, USGS, FAO, NPS, NRCAN, GeoBase, Kadaster NL, Ordnance Survey, Esri Japan, METI, Esri China (Hong Kong), and the GIS User Community'
    );

    // Initialize the map with the default layer
    this.map = L.map('map', {
      center: [51.505, -0.09],
      zoom: 13,
      layers: [openStreetMapLayer]
    });

    // Add layer controls
    const baseMaps = {
      'OpenStreetMap': openStreetMapLayer,
      'Satellite': satelliteLayer,
      'Esri': Esri_WorldTopoMap
    };

    const overlays = {
      'Regions': this.regionsLayer,
      'Provinces': this.provincesLayer,
      'Geo-Location': this.markersLayer
    };

    L.control.layers(baseMaps, overlays).addTo(this.map);

    // Add fullscreen control
    // @ts-ignore
    L.control.fullscreen({
      position: 'topright', // Position of the fullscreen button
      title: {
        'false': 'View Fullscreen',
        'true': 'Exit Fullscreen'
      }
    }).addTo(this.map);

    // @ts-ignore
    L.Control.geocoder().addTo(this.map);
    L.control.scale().addTo(this.map)


    // Geolocation feature
    this.getCurrentPosition().subscribe((position: any) => {
      this.map.flyTo([position.latitude, position.longitude], 13);
      const circle = L.circle([position.latitude, position.longitude], {
        color: 'black',
        fillColor: '#ff0000',
        fillOpacity: 1,
        radius: 300
      }).addTo(this.map);
      circle.addTo(this.markersLayer);
    });


  }

// Utility to create tile layers
  private createTileLayer(url: string, attribution: string): L.TileLayer {
    return L.tileLayer(url, {
      maxZoom: 18,
      attribution: attribution
    });
  }


  // Add polygons for regions
  private loadRegions(): void {
    this.regionsService.getRegions().subscribe((regions) => {
      regions.forEach((region: any) => {
        const polygon = L.polygon(this.parseCoordinates(region.geom), {
          color: 'blue',
          weight: 2,
          fillOpacity: 0.5
        });
        polygon.bindPopup(`<b>${region.name}</b><br>Area: ${region.area}`);
        polygon.addEventListener('click', () => {
          // Reset the last clicked polygon's style
          if (this.lastClickedPolygon) {
            this.lastClickedPolygon.setStyle({
              color: 'blue',
              weight: 2,
              fillOpacity: 0.5
            });
          }

          // Update the clicked polygon's style
          polygon.setStyle({
            color: 'red',
            weight: 3,
            fillOpacity: 0.7
          });

          // Store the reference to the currently clicked polygon
          this.lastClickedPolygon = polygon;
        })
        polygon.addTo(this.regionsLayer);
      });
      this.regionsLayer.addTo(this.map);
    });
  }

  // Add polygons for regions
  private loadProvinces(): void {
    this.provincesService.getProvinces().subscribe((provinces) => {
      provinces.forEach((province: any) => {
        const polygon = L.polygon(this.parseCoordinates(province.geom), {
          color: 'yellow',
          weight: 2,
          fillOpacity: 0.5
        });
        polygon.bindPopup(`<b>${province.name}</b><br>Area: ${province.area}`);
        polygon.addEventListener('click', () => {
          // Reset the last clicked polygon's style
          if (this.lastClickedPolygonProvince) {
            this.lastClickedPolygonProvince.setStyle({
              color: 'yellow',
              weight: 2,
              fillOpacity: 0.5
            });
          }

          // Update the clicked polygon's style
          polygon.setStyle({
            color: 'red',
            weight: 3,
            fillOpacity: 0.7
          });

          // Store the reference to the currently clicked polygon
          this.lastClickedPolygonProvince = polygon;
        })
        polygon.addTo(this.provincesLayer);
      });
      this.provincesLayer.addTo(this.map);
    });
  }

  // Parse WKT coordinates to Leaflet LatLng array
  private parseCoordinates(wkt: string): L.LatLngExpression[] {
    const coordsString = wkt.replace('POLYGON ((', '').replace('))', '');
    return coordsString.split(',').map(coord => {
      const [lng, lat] = coord.trim().split(' ').map(Number);
      return [lat, lng];
    });
  }

  // Add a marker with a custom icon
  private addMarker(lat: number, lng: number, popupText: string, iconUrl: string) {
    const customIcon = L.icon({
      iconUrl: iconUrl,
      iconSize: [38, 38],
      iconAnchor: [19, 38],
      popupAnchor: [0, -30]
    });

    const marker = L.marker([lat, lng], {icon: customIcon}).bindPopup(popupText);
    marker.addTo(this.markersLayer);
    this.markersLayer.addTo(this.map);
    return marker;
  }

  // Get the current geolocation
  private getCurrentPosition(): any {
    return new Observable((observer) => {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition((position) => {
          observer.next({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
          });
          observer.complete();
        });
      } else {
        observer.error();
      }
    });
  }

  // Add event listeners for user interactions
  private addEventListeners(): void {
    this.map.on('click', (e: any) => {
      const lat = e.latlng.lat;
      const lng = e.latlng.lng;

      // Remove the previous marker if it exists
      if (this.lastMarker) {
        this.map.removeLayer(this.lastMarker);
        this.markersLayer.removeLayer(this.lastMarker)
      }

      const popup = `<b>lat:${lat.toFixed(4)}-long:${lng.toFixed(4)}</b>`
      this.lastMarker = this.addMarker(lat, lng, popup, 'assets/leaflet/marker.png');
      // Add the new marker and store its reference
    });
  }
}
