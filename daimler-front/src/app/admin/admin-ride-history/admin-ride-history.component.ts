import { Component, ViewChild } from '@angular/core';
import { MatSelectChange } from '@angular/material/select';
import { DetailedRidePreviewComponent } from 'src/app/common/detailed-ride-preview/detailed-ride-preview.component';
import { RouteHistoryCardComponent, PageReguestArgs } from 'src/app/common/route-history-card/route-history-card.component';
import { UsersPreviewCardComponent } from 'src/app/common/users-preview-card/users-preview-card.component';
import { RideDTO } from 'src/app/entities/rides';
import { DriverService } from 'src/app/services/driver.service';
import { PassengerService } from 'src/app/services/passenger.service';

interface Role {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-admin-ride-history',
  templateUrl: './admin-ride-history.component.html',
  styleUrls: ['./admin-ride-history.component.css']
})
export class AdminRideHistoryComponent {

  constructor(private passengerService: PassengerService, private driverService: DriverService) {}

  readonly ROLE_DRIVER = 'driver';
  readonly ROLE_PASSENGER = 'passenger';
  readonly ITEMS_PER_PAGE = 3;

  roles: Role[] = [
    {value: this.ROLE_DRIVER, viewValue: 'Driver'},
    {value: this.ROLE_PASSENGER, viewValue: 'Passenger'}
  ]

  role: string = this.ROLE_DRIVER;

  @ViewChild("basicHistoryCard") basicHistoryCard!: RouteHistoryCardComponent;
  @ViewChild("passengersPreview") passengersPreviewCard!: UsersPreviewCardComponent;
  @ViewChild("detailsCard") detailsCard!: DetailedRidePreviewComponent;
  
  roleSelectionChanged(event : MatSelectChange) {
    this.role = event.value;
  }

  pageRequested(request : PageReguestArgs) {
    this.basicHistoryCard.userId = request.id;
    (this.role == this.ROLE_DRIVER ? this.driverService : this.passengerService).getRides(request.id, request.page, this.ITEMS_PER_PAGE, request.sortBy, 'from', 'to').subscribe((res) => {
      this.basicHistoryCard.setPage = {
        page : request.page,
        rides : res
      };
    })
  }
  
  detailsRequested(ride : RideDTO) {
    this.detailsCard.setRide = ride;
  }

  userSelected(value : string) {
    (this.role == this.ROLE_DRIVER ? this.driverService : this.passengerService).get(Number.parseInt(value)).subscribe({
      next: res => {
        console.log(res);
        this.passengersPreviewCard.Users.pop();
        this.passengersPreviewCard.Users.push(res);
        this.pageRequested({
          id: res.id,
          page: 1,
          itemsPerPage: 3,
          sortBy: 'id'
        });
      },
      error: _ => this.resetPage()
    });
  }

  onFieldFocus() {
    this.resetPage()
  }

  private resetPage() {
    this.passengersPreviewCard.Users.pop();
    this.basicHistoryCard.clearForm();
    this.detailsCard.clearForm();
  }

}
