import { Component, ViewChild } from '@angular/core';
import { DetailedRidePreviewComponent } from 'src/app/common/detailed-ride-preview/detailed-ride-preview.component';
import { UsersPreviewCardComponent } from 'src/app/common/users-preview-card/users-preview-card.component';
import { RideDTO } from 'src/app/entities/rides';
import { DriverService } from 'src/app/services/driver.service';
import { PassengerService } from 'src/app/services/passenger.service';
import { PageReguestArgs, RouteHistoryCardComponent } from '../../common/route-history-card/route-history-card.component';

@Component({
  selector: 'app-passenger-ride-history',
  templateUrl: './passenger-ride-history.component.html',
  styleUrls: ['./passenger-ride-history.component.css']
})
export class PassengerRideHistoryComponent {

  constructor(private passengerService : PassengerService, private driverService : DriverService) {}

  @ViewChild("basicHistoryCard") basicHistoryCard!: RouteHistoryCardComponent;
  @ViewChild("driverPreview") usersPreviewCard!: UsersPreviewCardComponent;
  @ViewChild("detailsCard") detailsCard!: DetailedRidePreviewComponent;
  
  pageRequested(request : PageReguestArgs) {
    this.passengerService.getRides(request.id, request.page, request.itemsPerPage, request.sortBy, 'from', 'to').subscribe((res) => {
      this.basicHistoryCard.setPage = {
        page : request.page,
        rides : res
      };
    })
  }

  detailsRequested(ride : RideDTO) {
    this.driverService.get(ride.driver.id).subscribe((driver) => {
      this.usersPreviewCard.Users.pop();
      this.usersPreviewCard.Users.push(driver);
      this.detailsCard.setRide = ride;
    })
  }
}
