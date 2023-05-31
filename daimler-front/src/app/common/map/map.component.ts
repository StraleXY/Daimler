import { AfterViewInit, Component, EventEmitter, Input, Output } from '@angular/core';
import { MarkerService, grayOptions, grayIcon, greenOptions} from 'src/app/services/marker-service.service'
import { UnregisteredService } from 'src/app/services/unregistered.service';

var L = require('leaflet');
require('leaflet-routing-machine');
require('mapbox-gl-leaflet');
require('leaflet-providers');


@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements AfterViewInit {
    private map: any;
    private start: any;
    private end: any;
    private firstWaypoint: any;
    private secondWaypoint: any;
    private firstTempWaypoint: any = null;
    private secondTempWaypoint: any = null;
    clickedOnMap: boolean = false;

    @Input() waypoints: any = {};
    @Input() isRoute: boolean = false;
    @Input() isMarker: boolean = false;
    @Input() isInteractive: boolean = true;
    @Input() isDriver: boolean = false;
    @Input() isDraggable: boolean = true;
    @Input() isVehicleMarker: boolean = false;
    @Input() vehicleLocation: any = {};
    vehicleMarker: any = null;
    @Input() set setPanic(vehicleId: number) {
        if (vehicleId == 0) return;
        this.markerService.makePanicPin(vehicleId, this.map);
    }
    routingControl: any = null;

    @Output() first = new EventEmitter<string>();
    @Output() second = new EventEmitter<string>();

    private initMap(): void {
        this.start = L.latLng(45.25382, 19.84911);
        this.end =  L.latLng(45.2571, 19.8157);

        this.map = L.map('map', {
            center: [ 45.2556, 19.8271 ],
            zoom: 14
        });
        L.tileLayer.provider('Stadia.AlidadeSmooth').addTo(this.map);

    }

    constructor(private markerService: MarkerService, private unregisteredService: UnregisteredService) {
    }

    ngAfterViewInit(): void {
        this.initMap();
        if (this.isMarker) this.markerService.makeMarkers(this.map);
        if (this.isRoute) this.drawRoute();
        if (!this.isInteractive) return;
        this.map.on('click', (event: any) => {
            let mapLocation = event.latlng;
            this.unregisteredService.reverseDecode(mapLocation)
            .subscribe((res: any) => {
                if (res.address.house_number === undefined) {
                    alert("Chosen location's house number is undefined. Please try again");
                    return;
                }
                if (!this.clickedOnMap) {
                    this.clickedOnMap = true;
                    this.first.emit(res.address.road + " " + res.address.house_number);
                    if (this.firstTempWaypoint != null) {this.map.removeLayer(this.firstTempWaypoint)}
                    this.firstTempWaypoint = L.marker([mapLocation.lat, mapLocation.lng], grayOptions);
                    this.firstTempWaypoint.addTo(this.map);
                } else {
                    this.clickedOnMap = false;
                    this.second.emit(res.address.road + " " + res.address.house_number);
                    if (this.secondTempWaypoint != null) {this.map.removeLayer(this.secondTempWaypoint)}
                    this.secondTempWaypoint = L.marker([mapLocation.lat, mapLocation.lng], grayOptions);
                    this.secondTempWaypoint.addTo(this.map);
                }
            });
        });
    }

    ngOnChanges(): void {
        if (this.isRoute) this.drawRoute();
        if (this.isVehicleMarker || this.isDriver) this.makeMarker();
    }

    makeMarker(): void {
        if (Object.keys(this.vehicleLocation).length === 0) return;
        if (this.vehicleMarker != null) this.map.removeLayer(this.vehicleMarker);
        this.vehicleMarker = L.marker([this.vehicleLocation[1], this.vehicleLocation[0]], greenOptions);
        this.vehicleMarker.addTo(this.map);
    }

    drawRoute(): void {
        if (Object.keys(this.waypoints).length === 0) return;
        if (this.firstWaypoint != null && this.secondWaypoint != null && this.waypoints != null && this.waypoints.first != null && this.waypoints.second != null &&
            this.firstWaypoint.lat == this.waypoints.first.latitude && this.firstWaypoint.lng == this.waypoints.first.longitude
            && this.secondWaypoint.lat == this.waypoints.second.latitude && this.secondWaypoint.lng == this.waypoints.second.longitude)
            return;
        if (this.routingControl != null) this.map.removeControl(this.routingControl);
        if (this.firstTempWaypoint != null) {this.map.removeLayer(this.firstTempWaypoint)}
        if (this.secondTempWaypoint != null) {this.map.removeLayer(this.secondTempWaypoint)}
        if (this.map == null || this.map == undefined) return;
        this.firstWaypoint = L.latLng(this.waypoints.first.latitude, this.waypoints.first.longitude, grayOptions);
        this.secondWaypoint = L.latLng(this.waypoints.second.latitude, this.waypoints.second.longitude, grayOptions);
        this.routingControl = L.Routing.control({
            waypoints: [
                this.firstWaypoint,
                this.secondWaypoint
            ],
            lineOptions: {
                styles: [{color: '#212121', opacity: 1, weight: 4}]
            },
            createMarker: (i: number, waypoint: any, n: number) => {
                const marker = L.marker(waypoint.latLng, {
                    draggable: this.isDraggable,
                    bounceOnAdd: false,
                    icon: grayIcon
                });
                marker.on("dragend", (event: any) => {
                    let mapLocation = event.target._latlng;
                    this.unregisteredService.reverseDecode(mapLocation)
                    .subscribe((res: any) => {
                        if (res.address.house_number === undefined) {
                            alert("Chosen location's house number is undefined. Please try again");
                            return;
                        }
                        if (i == 0)
                            this.first.emit(res.address.road + " " + res.address.house_number);
                        else
                            this.second.emit(res.address.road + " " + res.address.house_number);

                    });
                });
                return marker;
            },
        });
        this.routingControl.addTo(this.map);
        // These are not needed, fit bounds works as expected
        //this.map.flyTo(L.latLngBounds(this.firstWaypoint, this.secondWaypoint));
        //this.map.panTo(L.latLngBounds(this.firstWaypoint, this.secondWaypoint));
        this.map.fitBounds(L.latLngBounds(this.firstWaypoint, this.secondWaypoint));
    }

    calculate(): void {
        // I'll maybe need this in the future for routes
        L.Routing.control({
            waypoints: [
                this.start,
                this.end
            ],
        }).addTo(this.map);

        var wayPoint1 = L.latLng(45.25382, 19.84911);
        var wayPoint2 = L.latLng(45.2571, 19.8157);

        var rWP1 = new L.Routing.Waypoint;
        rWP1.latLng = wayPoint1;

        var rWP2 = new L.Routing.Waypoint;
        rWP2.latLng = wayPoint2;

        var myRoute = L.Routing.osrmv1();
        myRoute.route([rWP1, rWP2], function(err: any, routes:any) {
            let distance = routes[0].summary.totalDistance;
            console.log('routing distance: ' + distance);
        });

        this.map.fitBounds(L.latLngBounds(wayPoint1, wayPoint2));
    }
}

