import {Injectable} from '@angular/core';
import L, {LayerGroup, Polygon, PolylineOptions} from 'leaflet';

@Injectable({
  providedIn: 'root'
})
export class MapService {

  constructor() {
  }


  private map: L.Map | null = null;

  initializeMap(mapId: string, options: L.MapOptions): L.Map {
    this.map = L.map(mapId, options);
    return this.map;
  }

  createTileLayer(url: string, attribution: string): L.TileLayer {
    return L.tileLayer(url, {
      maxZoom: 18,
      attribution: attribution,
    });
  }

  addLayersControl(
    map: L.Map,
    baseLayers: Record<string, L.TileLayer>,
    overlays: Record<string, L.LayerGroup>,
    defaultBaseLayer: L.TileLayer
  ): void {
    // Add the default base layer to the map
    defaultBaseLayer.addTo(map);

    // Combine base layers and overlays into a single layers control
    const layersControl = L.control.layers(baseLayers, overlays);
    layersControl.addTo(map);

    // Add overlays to the map
    Object.values(overlays).forEach((overlay) => overlay.addTo(map));
  }

  locateUser(map: L.Map): void {
    map.locate({setView: true, watch: true, maxZoom: 10})
      .on('locationfound', (e) => {
        console.log(e.latlng);
      })
      .on('locationerror', (e) => {
        console.error(e);
        alert('Location access has been denied.');
      });
  }

  parseCoordinates(wkt: string): L.LatLngExpression[] {
    const coordsString = wkt.replace('POLYGON ((', '').replace('))', '');
    return coordsString.split(',').map((coord) => {
      const [lng, lat] = coord.trim().split(' ').map(Number);
      return [lat, lng];
    });
  }

  parseWktPoint(wkt: string): [number, number] | null {
    // Parse WKT format POINT (-7.6228 33.54138)
    const match = wkt.match(/POINT\s*\(\s*(-?\d+\.?\d*)\s+(-?\d+\.?\d*)\s*\)/);
    if (match) {
      const lng = parseFloat(match[1]);
      const lat = parseFloat(match[2]);
      return [lat, lng];
    }
    console.error('Invalid WKT format:', wkt);
    return null;
  }

  addPolygon(map: L.Map | null, wkt: string, options: PolylineOptions, layerGroup: LayerGroup): Polygon {
    const polygon = L.polygon(this.parseCoordinates(wkt), options);
    polygon.addTo(layerGroup);
    layerGroup.addTo(map!);
    return polygon;
  }

  addScaleControl(map: L.Map): void {
    L.control.scale().addTo(map);
  }

  getMap() {
    return this.map;
  }

  addGeocoder(map: L.Map): void {
    // @ts-ignore
    L.Control.geocoder({
      defaultMarkGeocode: false,
    }).on('markgeocode', (e: any) => {
      const bbox = e.geocode.bbox;
      const poly = L.polygon([
        bbox.getSouthEast(),
        bbox.getNorthEast(),
        bbox.getNorthWest(),
        bbox.getSouthWest(),
      ]).addTo(map);
      map.fitBounds(poly.getBounds());
    }).addTo(map);
  }


  addMarker(lat: number, lng: number, popupText: string, iconUrl: string, markersLayer: L.LayerGroup): L.Marker {
    const customIcon = L.icon({
      iconUrl: iconUrl,
      iconSize: [38, 38],
      iconAnchor: [19, 38],
      popupAnchor: [0, -30],
    });

    const marker = L.marker([lat, lng], {icon: customIcon}).bindPopup(popupText);
    marker.addTo(markersLayer);
    markersLayer.addTo(this.map!);
    return marker;
  }
}
