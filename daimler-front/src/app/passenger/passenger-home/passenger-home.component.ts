import { Component, EventEmitter, OnInit, ViewChild } from '@angular/core';
import { EstimationDTO, LatLon } from '../../common/get-a-ride/get-a-ride.component';
import { trigger, state, style, animate, transition } from '@angular/animations';
import { PassengerService } from 'src/app/services/passenger.service';
import { BabyPets } from '../more-ride-settings/more-ride-settings.component';
import { HttpErrorResponse } from '@angular/common/http';
import { RideDTO } from 'src/app/entities/rides';
import { WebsocketService } from 'src/app/services/websocket.service';
import { DriverComingComponent } from 'src/app/driver/driver-coming/driver-coming.component';
import { LinkedPassenger, LinkedRidePersonsComponent } from '../linked-ride-persons/linked-ride-persons.component';
import { DriverService } from 'src/app/services/driver.service';
import { RideService } from 'src/app/services/ride.service';
import { MatDialog } from '@angular/material/dialog';
import { RateRideDialogComponent } from '../rate-ride-dialog/rate-ride-dialog.component';
import { Router } from '@angular/router';
var L = require('leaflet');

@Component({
  selector: 'app-passenger-home',
  templateUrl: './passenger-home.component.html',
  styleUrls: ['./passenger-home.component.css'],
  animations: [
    trigger("inOutPaneAnimation", [
      transition(":enter", [
        style({ opacity: 0, transform: "translateY(35%)" }),
        animate(
          "150ms ease-in",
          style({ opacity: 1, transform: "translateY(0)" })
        )
      ]),
      transition(":leave", [
        style({ opacity: 1, transform: "translateX(0)" }),
        animate(
          "250ms ease-out",
          style({ opacity: 0, transform: "translateX(100%)" })
        )
      ])
    ])
  ]
})
export class PassengerHomeComponent implements OnInit {

  @ViewChild("driverComingCard") driverComingCard!: DriverComingComponent;
  @ViewChild("linkedPeople") linkedPeopleCard!: LinkedRidePersonsComponent;
  getRouteVisibility = true;
  confirmRideVisibility = false;
  routeCompactVisibility = false;
  passengerCountVisibility = false;
  moreSettingsVisibility = false;
  linkedPersonsVisibility = false;
  driverComingVisibility = false;
  rideInProgressVisibility = false;
  scheduledVisibility = false;

  isOrdering = true;
  isToggled = false;
  first = "";
  second = "";
  ride : RideDTO = {
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
    passengers: [],
    estimatedTimeInMinutes: 0,
    vehicleType: '',
    petTransport: false,
    babyTransport: false,
    rejection: {
      reason: '',
      timeOfRejection: ''
    },
    locations: [],
    status: '',
    distance: 0,
    scheduledTimestamp: 0
  }

  animationLength = 350;

  websocketService: WebsocketService;
  private eventCallback: EventEmitter<any> = new EventEmitter<any>();

  constructor(private dialog: MatDialog, private passengerService: PassengerService,
              private driverService: DriverService, private rideService: RideService,
              private router: Router) {
    let passengerId = Number.parseInt(localStorage.getItem('userId') ?? '');
    this.inviterId = passengerId;
    this.passengerService.get(Number.parseInt(localStorage.getItem('userId') ?? '')).subscribe((res) => {
      this.passengers.push({"id": passengerId, "email": res.email})
    });
    this.websocketService = new WebsocketService();
    this.websocketService.eventCallback = this.eventCallback;
    this.websocketService._connect();
    this.eventCallback.subscribe((res: any) => {
      this.onMessageReceived(res);
    });
    this.restoreSession(0);
  }

  ngOnInit() {
      this.first = history.state.data.departure;
      this.second = history.state.data.destination;
  }

  onMessageReceived(message: any) {
    console.log(message);
    if (message.split(',')[0] == 'invite')
        this.linkedPeopleCard.response(message.split(',')[1], message.split(',')[2], message.split(',')[3]);
    else if (message.split(',')[0] == 'ride') {
        this.restoreSession(Number(message.split(',')[1]));
    } else if (message.split(',')[0] == 'vehicle') {
        this.currentVehicleLocation = [message.split(',')[1], message.split(',')[2]];
    }
  }

  toggle(toggled: boolean) : void{
    this.routeCompactVisibility = toggled;
    this.confirmRideVisibility = toggled;
  }

  end(rideId: number) {
    this.rideInProgressVisibility = false;
    this.router.navigate(['passenger/inbox'], {state: {data: {'rideId': rideId }}});
  }

  estimation: EstimationDTO = {estimatedTimeInMinutes:0, distance:0, estimatedCost:0};
  waypoints: any = {}
  passengers: any = []
  currentVehicleLocation: any = []
  vehicleType: any = ""
  babyPets: BabyPets = {baby:false, pets:false, isFavourite: false, scheduled: Date.now()};
  inviterId: number = 0;
  addressFrom: string = "";
  addressTo: string = "";

  estimate(est: EstimationDTO) : void{
    this.estimation = est;
    this.first = "";
    this.second = "";
  }

  waypointEvent(e: any) {
    this.waypoints = e;
  }

  // Show count settings
  confirmeClick() {
    this.getRouteVisibility = false;
    this.confirmRideVisibility = false;
    setTimeout(() => {
      this.passengerCountVisibility = true;
    }, this.animationLength);
  }

  // Show linked rides
  groupClick() {
    this.passengerCountVisibility = false;
    this.addressFrom = this.waypoints.first.address,
    this.addressTo = this.waypoints.second.address,
    setTimeout(() => {
      this.linkedPersonsVisibility = true;
    }, this.animationLength);
  }

  // Show more settings
  moreSettingsClick() {
    if (this.passengerCountVisibility) this.passengerCountVisibility = false;
    if (this.linkedPersonsVisibility) this.linkedPersonsVisibility = false;
    setTimeout(() => {
      this.moreSettingsVisibility = true;
    }, this.animationLength);
  }

  // Show driver coming
  driverComingClick() {
    this.moreSettingsVisibility = false;
    this.routeCompactVisibility = false;
    setTimeout(() => {
    }, this.animationLength);
  }

  // Ride in progress
  rideInProgressClick() {
    this.driverComingVisibility = false;
    setTimeout(() => {
      this.rideInProgressVisibility = true;
    }, this.animationLength);
  }

  // Show rate ride
  rateRideClick() {
    const dialogRef = this.dialog.open(RateRideDialogComponent, {data: {
        passengerId: Number.parseInt(localStorage.getItem('userId') ?? ''),
        driverId: this.ride.driver.id,
        rideId: this.ride.id
      }
    });

    dialogRef.afterClosed().subscribe(review => {
        window.location.reload();
    });
  }

  setFirst(e: any) {
    this.first = e;
  }

  setSecond(e: any) {
    this.second = e;
  }

  createRide(babyPets: BabyPets) {
    this.babyPets = babyPets;
    if (this.babyPets.pets == null) this.babyPets.pets = false;
    if (this.babyPets.baby == null) this.babyPets.baby = false;
    let req = {
      "locations": [
          {
              "departure": this.waypoints.first,
              "destination": this.waypoints.second
          }
      ],
      "passengers": this.passengers,
      "vehicleType": this.vehicleType,
      "babyTransport": this.babyPets.baby,
      "petTransport": this.babyPets.pets,
      "scheduledTimestamp": this.babyPets.scheduled
    }
    console.log(req);
    this.passengerService.createRide(req).subscribe({
      next: res => {
        if (this.babyPets.isFavourite == true) this.addToFavourites(res);
        this.restoreSession(res.id);
      },
      error: err => {
        alert("Driver could not be found.");
        window.location.reload();
      }
    })
  }

  addToFavourites(ride: any) {
      this.passengerService.postFavoriteRoute(Number.parseInt(localStorage.getItem('userId') ?? ''), ride.locations[0].departure.id, ride.locations[0].destination.id)
      .subscribe((res: any) => {
      });
  }

  vehicleTypeEvent(e: any) {
      this.vehicleType = e.value;
  }

  passengerList(e: LinkedPassenger[]) {
      for (let passenger of e) {
          if (!passenger.accepted) continue
          this.passengers.push({"id": passenger.id, "email": passenger.email})
      }
  }

  restoreSession(rideId: any): void {
      if (rideId == 0) {
          this.passengerService.getActiveRide(localStorage.getItem('userId') ?? "")
          .subscribe((ride: any) => {
              this.isOrdering = false;
              this.ride = ride;
              this.getRouteVisibility = false;
              this.driverComingVisibility = true;
              this.driverService.getVehicle(ride.driver.id)
              .subscribe((res: any) => {
                  if (this.ride.status == "ACCEPTED") {
                      this.driverComingClick();
                      let first: LatLon = res.currentLocation;
                      let second: LatLon = this.ride.locations[0].departure;
                      console.log(first, second);
                      this.waypoints = {first, second}
                  } else if(this.ride.status == "PENDING") {
                    this.driverComingClick();
                    console.log("PENDING RDIE")
                    console.log(ride);
                  } else if (this.ride.status == "ACTIVE") {
                      this.rideInProgressClick();
                      let first: LatLon = this.ride.locations[0].departure;
                      let second: LatLon = this.ride.locations[0].destination;
                      this.waypoints = {first, second}
                  } else if (this.ride.status == "FINISHED") {
                    this.isOrdering = false;
                    this.rateRideClick();
                  } else if (this.ride.status == "PANIC" || this.ride.status == "REJECTED") {
                    this.isOrdering = false;
                    window.location.reload();
                  }
                  this.currentVehicleLocation = [res.currentLocation.longitude, res.currentLocation.latitude]
              });
          });
      } else {
          this.rideService.getRide(rideId)
            .subscribe((ride: any) => {
                this.isOrdering = false;
                this.ride = ride;
                this.getRouteVisibility = false;
                if (this.ride.status != "PENDING") {
                    this.scheduledVisibility = false;
                    this.driverComingVisibility = true;
                    this.driverService.getVehicle(ride.driver.id)
                      .subscribe((res: any) => {
                              if (this.ride.status == "ACCEPTED") {
                                this.driverComingClick();
                                let first: LatLon = res.currentLocation;
                                let second: LatLon = this.ride.locations[0].departure;
                                this.waypoints = {first, second}
                              } else if (this.ride.status == "ACTIVE") {
                                this.rideInProgressClick();
                                let first: LatLon = this.ride.locations[0].departure;
                                let second: LatLon = this.ride.locations[0].destination;
                                this.waypoints = {first, second}
                              } else if (this.ride.status == "FINISHED") {
                                this.isOrdering = false;
                                this.rateRideClick();
                              }
                              this.currentVehicleLocation = [res.currentLocation.longitude, res.currentLocation.latitude]
                    });
                } else {
                    this.scheduledVisibility = true;
                    this.driverComingClick();
                }
          });
      }
  }
}
