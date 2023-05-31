import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import * as L from 'leaflet';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export const grayPin = 'assets/gray-pin.png';
export const greenPin = 'assets/green-pin.png';
export const redPin = 'assets/red-pin.png';
export const panicPin = 'assets/panic.gif'

export const grayIcon = L.icon({
  iconUrl: grayPin,
  iconSize: [50, 50],
  iconAnchor: [25, 45],
  popupAnchor: [20, 20],
  tooltipAnchor: [20, 20],
});

export const greenIcon = L.icon({
  iconUrl: greenPin,
  iconSize: [50, 50],
  iconAnchor: [25, 45],
  popupAnchor: [20, 20],
  tooltipAnchor: [20, 20],
});

export const redIcon = L.icon({
  iconUrl: redPin,
  iconSize: [50, 50],
  iconAnchor: [25, 45],
  popupAnchor: [20, 20],
  tooltipAnchor: [20, 20],
});

export const panicIcon = L.icon({
    iconUrl: panicPin,
    iconSize: [50, 50],
    iconAnchor: [25, 45],
    popupAnchor: [20, 20],
    tooltipAnchor: [20, 20],
  });

export const grayOptions = { icon: grayIcon, clickable: false, draggable: false }
export const greenOptions = { icon: greenIcon, clickable: false, draggable: false }
export const redOptions = { icon: redIcon, clickable: false, draggable: false }
export const panicOptions = { icon: panicIcon, clickable: false, draggable: false }

interface LatLong {
    vehicleId: number;
    latitude: number;
    longitude: number;
    busy: boolean;
}

interface Locations {
    locations: Array<LatLong>
}

@Injectable({
  providedIn: 'root'
})

export class MarkerService {

  constructor(private http: HttpClient) { }

  getLocations(): Observable<any> {
      return this.http.get<string>(environment.apiHost + 'vehicle');
  }

  getVehicle(id: any): Observable<any> {
      return this.http.get<string>(environment.apiHost + "driver/" + id + '/vehicle');
  }

  private markers: Map<number, L.Marker> = new Map<number, L.Marker>();

  makeMarkers(map: L.Map): void {
      this.getLocations()
      .subscribe((res: Locations) => {
          res.locations.forEach((entry) => {
              if (entry.busy) {
                  let marker = L.marker([entry.latitude, entry.longitude], redOptions);
                  marker.addTo(map);
                  this.markers.set(entry.vehicleId, marker);
              } else {
                  let marker = L.marker([entry.latitude, entry.longitude], greenOptions);
                  marker.addTo(map);
                  this.markers.set(entry.vehicleId, marker);
              }
          });
      });
  }

  makeDriverMarker(map: L.Map): void {
      this.getVehicle(Number.parseInt(localStorage.getItem('userId') ?? ''))
      .subscribe((res: any) => {
          let marker = L.marker([res.currentLocation.latitude, res.currentLocation.longitude], grayOptions);
          marker.addTo(map);
      });
  }
  
  makePanicPin(vehicleId: number, map: L.Map) {
    let oldMarker = this.markers.get(vehicleId);
    if(oldMarker == undefined) return;
    oldMarker?.remove();
    let marker = L.marker([oldMarker.getLatLng().lat, oldMarker.getLatLng().lng], panicOptions);
    let newMarker = marker.addTo(map);
    this.markers.set(vehicleId, newMarker);
  }
}
