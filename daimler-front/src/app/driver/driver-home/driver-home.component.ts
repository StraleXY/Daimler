import { Component, EventEmitter } from '@angular/core';
import { LatLon } from 'src/app/common/get-a-ride/get-a-ride.component';
import { LocationDTO, RideDTO } from 'src/app/entities/rides';
import { RideService } from 'src/app/services/ride.service';
import { WebsocketService } from 'src/app/services/websocket.service';
import { DriverService } from 'src/app/services/driver.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-driver-home',
  templateUrl: './driver-home.component.html',
  styleUrls: ['./driver-home.component.css']
})
export class DriverHomeComponent {
    showRouteDetails: boolean = false;
    ride: RideDTO = {
        id: 0,
        startTime: '',
        endTime: '',
        totalCost: 0,
        driver: {
            id: 0,
            email: '',
            name: '',
            surname: '',
            profilePicture: ''
        },
        passengers: [ ],
        estimatedTimeInMinutes: 0,
        vehicleType: '',
        petTransport: true,
        babyTransport: false,
        rejection: {
            timeOfRejection: '',
            reason: ''
        },
        locations: [],
        status: '',
        distance: 0,
        scheduledTimestamp: 0
    }
  active: boolean = true
  isRejection: boolean = false;
  hasActiveRide: boolean = false;
  waypoints: any = {}
  currentVehicleLocation: any = []
  websocketService: WebsocketService;
  vehicleLocation: LocationDTO = {id: 0, address: "", latitude: 0, longitude: 0}
  rejectionText: string = "";
  private eventCallback: EventEmitter<any> = new EventEmitter<any>();

  constructor(private rideService: RideService, private driverService: DriverService, private router: Router) {
    this.websocketService = new WebsocketService();
    this.websocketService.eventCallback = this.eventCallback;
    this.active = localStorage.getItem("driverStatus") === 'active';
    this.websocketService._connect();
    this.eventCallback.subscribe((res: any) => {
        this.onMessageReceived(res);
      });
    this.restoreSession();
  }

  setActive(newActive: boolean) {
    localStorage.setItem("driverStatus", newActive ? 'active' : 'inactive');
    if (this.active != newActive) {
      if (newActive) {
        let driverId = localStorage.getItem("userId")??"";
        this.driverService.startWorkingHour(driverId).subscribe(response => {
          localStorage.setItem('workingHourId', response.id);
          this.active = newActive;
        });
      } else {
        this.driverService.endWorkingHour(localStorage.getItem('workingHourId')??"").subscribe(response => {
          localStorage.removeItem('workingHourId');
          this.active = newActive;
        });
      }
    }
  }

  waypointEvent(e: any) {
      this.waypoints = e;
  }

  onMessageReceived(message: any) {
      if (message.split(",")[0] == "ride") {
          this.rideService.getRide(message.split(",")[1])
          .subscribe((res: RideDTO) => {
              this.hasActiveRide = true;
              this.showRouteDetails = true;
              console.log(res);
              this.ride = res;
              this.driverService.getVehicle(res.driver.id).subscribe(vehicle => {
                  console.log(vehicle.currentLocation);
                  if (res.status == "PENDING" || res.status == "ACCEPTED") {
                      let first: LatLon = vehicle.currentLocation;
                      let second: LatLon = res.locations[0].departure;
                      this.waypoints = {first, second}
                  } else if (res.status == "ACTIVE") {
                      let first: LatLon = res.locations[0].departure;
                      let second: LatLon = res.locations[0].destination;
                      this.waypoints = {first, second}
                  } else if (this.ride.status == "FINISHED" || this.ride.status == "PANIC" || this.ride.status == "REJECTED") {
                    window.location.reload();
                  }
              });

          });
      } else if (message.split(',')[0] == 'vehicle') {
        this.currentVehicleLocation = [message.split(',')[1], message.split(',')[2]];
      }
  }

  cancelListener(e: boolean) {
      this.isRejection = !e;
  }

  rejectListener(e: boolean) {
      this.isRejection = false;
      this.rideService.reject(this.ride.id, this.rejectionText)
      .subscribe((res: any) => {
          alert("Rejection successful!");
          window.location.reload();
      });
  }

  rejectionTextListener(e: string) {
      this.rejectionText = e;
  }

  reject() {
      this.isRejection = true;
  }

  start() {
      this.rideService.start(this.ride.id)
      .subscribe((res: any) => {
          let first: LatLon = this.ride.locations[0].departure;
          let second: LatLon = this.ride.locations[0].destination;
          this.waypoints = {first, second}
      });
  }

  end() {
      this.ride.status = "FINISHED";
      this.rideService.end(this.ride.id)
      .subscribe((res: any) => {
          alert("RIDE FINISHED!");
          window.location.reload();
      });
  }

  panic() {
    this.ride.status = "PANIC";
    this.rideService.panic(this.ride.id, "Ended in panic.").subscribe(res => {
        alert("RIDE FINISHED WITH PANIC!");
        this.router.navigate(['driver/inbox'], {state: {data: {'rideId': this.ride.id }}});
    })
  }

  restoreSession(): void {
    this.driverService.getVehicle(Number(localStorage.getItem('userId')))
    .subscribe((res: any) => {
        this.vehicleLocation = res.currentLocation;
        this.currentVehicleLocation = [this.vehicleLocation.longitude, this.vehicleLocation.latitude]
        this.driverService.getActiveRide(localStorage.getItem('userId') ?? "")
        .subscribe((res: any) => {
            this.hasActiveRide = true;
            this.ride = res;
            if (this.ride.status == "PENDING" || this.ride.status == "ACCEPTED") {
                this.showRouteDetails = true
                let first: LatLon = this.vehicleLocation;
                let second: LatLon = res.locations[0].departure;
                this.waypoints = {first, second}
            } else if (this.ride.status == "ACTIVE") {
                this.showRouteDetails = true
                let first: LatLon = res.locations[0].departure;
                let second: LatLon = res.locations[0].destination;
                this.waypoints = {first, second}
            } else if (this.ride.status == "FINISHED" || this.ride.status == "PANIC" || this.ride.status == "REJECTED") {
              window.location.reload();
            }
        });
    });
  }

//   restoreSession(): void {
//     this.driverService.getActiveRide(localStorage.getItem('userId') ?? "")
//     .subscribe((res: any) => {
//         this.ride = res;
//         if (this.ride.status == "ACCEPTED") {
//             this.showRouteDetails = true
//             let first: LatLon = this.vehicleLocation;
//             let second: LatLon = res.locations[0].departure;
//             this.waypoints = {first, second}
//         } else if (this.ride.status == "ACTIVE") {
//             this.showRouteDetails = true
//             let first: LatLon = res.locations[0].departure;
//             let second: LatLon = res.locations[0].destination;
//             this.waypoints = {first, second}
//         }
//     });
//   }
}
