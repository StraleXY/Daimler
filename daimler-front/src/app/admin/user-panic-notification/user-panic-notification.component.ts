import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { RideDTO, UserInRideDTO } from 'src/app/entities/rides';
import { PanicRideDTO, UserFull } from 'src/app/entities/user';

@Component({
  selector: 'app-user-panic-notification',
  templateUrl: './user-panic-notification.component.html',
  styleUrls: ['./user-panic-notification.component.css']
})
export class UserPanicNotificationComponent {

  @Input() panicDTO!: PanicRideDTO;

  constructor(private router: Router) {}

  chatClick() {
    this.router.navigate(['admin/inbox'], {state: {data: {'rideId': this.panicDTO.ride.id }}});
  }
}