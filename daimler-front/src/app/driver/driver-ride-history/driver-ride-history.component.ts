import { Component, ViewChild } from '@angular/core';
import { DetailedRidePreviewComponent } from 'src/app/common/detailed-ride-preview/detailed-ride-preview.component';
import { PageReguestArgs, RouteHistoryCardComponent } from 'src/app/common/route-history-card/route-history-card.component';
import { UsersPreviewCardComponent } from 'src/app/common/users-preview-card/users-preview-card.component';
import { RideDTO } from 'src/app/entities/rides';
import { DriverService } from 'src/app/services/driver.service';
import { PassengerService } from 'src/app/services/passenger.service';

@Component({
  selector: 'app-driver-ride-history',
  templateUrl: './driver-ride-history.component.html',
  styleUrls: ['./driver-ride-history.component.css']
})
export class DriverRideHistoryComponent {
  
  constructor(private driverService : DriverService, private passengerService : PassengerService) {}

  @ViewChild("basicHistoryCard") basicHistoryCard!: RouteHistoryCardComponent;
  @ViewChild("passengersPreview") passengersPreviewCard!: UsersPreviewCardComponent;
  @ViewChild("detailsCard") detailsCard!: DetailedRidePreviewComponent;
  
  pageRequested(request : PageReguestArgs) {
    this.driverService.getRides(request.id, request.page, request.itemsPerPage, request.sortBy, 'from', 'to').subscribe((res) => {
      this.basicHistoryCard.setPage = {
        page : request.page,
        rides : res
      };
    })
  }
  
  detailsRequested(ride : RideDTO) {
    this.passengersPreviewCard.Users = [];
    ride.passengers.forEach(passengerShort => {
      this.passengerService.get(passengerShort.id).subscribe((passenger) => {
        this.passengersPreviewCard.Users.push(passenger);
      })
    });
    this.detailsCard.setRide = ride;
  }
}
