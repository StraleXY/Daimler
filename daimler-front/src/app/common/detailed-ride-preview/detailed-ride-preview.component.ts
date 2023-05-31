import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { CombinedReviewsDTO } from 'src/app/entities/reviews';
import { DepartureDestinationDTO, LocationDTO, RideDTO } from 'src/app/entities/rides';
import { RateRideDialogComponent } from 'src/app/passenger/rate-ride-dialog/rate-ride-dialog.component';
import { PassengerService } from 'src/app/services/passenger.service';
import { ReviewService } from 'src/app/services/review.service';
var L = require('leaflet');

export interface ReviewDialogData {
  rideId: number;
  passengerId: number;
  driverId: number;
}

@Component({
  selector: 'app-detailed-ride-preview',
  templateUrl: './detailed-ride-preview.component.html',
  styleUrls: ['./detailed-ride-preview.component.css']
})
export class DetailedRidePreviewComponent {

  constructor(private reviewService : ReviewService, private dialog: MatDialog,
              private passengerService : PassengerService, private router: Router) {}

  waypoints: any = {}

  passengerId : number = Number.parseInt(localStorage.getItem('userId') ?? '');

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
      timeOfRejection: '',
      reason: ''
    },
    locations: [],
    status: '',
    distance: 0,
    scheduledTimestamp: 0
  }

  reviews : CombinedReviewsDTO[] = []

  @Input() set setRide(value : RideDTO) {
    this.ride = value;
    this.waypoints = {first:this.ride.locations[0].departure,
        second:this.ride.locations[0].destination};
    console.log(this.ride.locations)
    this.reviewService.getRideReviews(value.id).subscribe((reviews) => {
      console.log(reviews);
      this.reviews = reviews;
    })
  }

  public clearForm() {
    this.setRide = {
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
        timeOfRejection: '',
        reason: ''
      },
      locations: [],
      status: '',
      distance: 0,
      scheduledTimestamp: 0
    };
  }

  @Input() showControls : boolean = true;

  public getLocationList(locations : DepartureDestinationDTO[]) : LocationDTO[] {
    let loc : LocationDTO[] = [];
    for(let i = 0; i < locations.length; i++) {
        loc.push(locations[i].departure);
        if(i == locations.length - 1) loc.push(locations[i].destination);
    }
    return loc;
  }

  public showRateDialogClick() {

    let passengerThatRated = this.reviews.map(r => r.driverReview.passenger.id);
    let alreadyRated = passengerThatRated.includes(this.passengerId);
    //TODO Check the date
    if(alreadyRated) return;

    const dialogRef = this.dialog.open(RateRideDialogComponent, {data: {
        passengerId: this.passengerId,
        driverId: this.ride.driver.id,
        rideId: this.ride.id
      }
    });

    dialogRef.afterClosed().subscribe(review => {
      if(review == undefined) return;
      this.reviews.push(review);
    });
  }

  public addToFavorites() {
    console.log(this.ride.locations[0].departure.id);
    console.log(this.ride.locations[0].destination.id);

    this.passengerService.postFavoriteRoute(this.passengerId, this.ride.locations[0].departure.id, this.ride.locations[0].destination.id).subscribe(route => {
      console.log(route);

    })
  }

  public orderAgainClick() {
    L.DomUtil.get('map').remove();
    this.router.navigate(['passenger/home'], {state: {data: {"departure": this.ride.locations[0].departure.address, "destination": this.ride.locations[0].destination.address}}});
  }
}
