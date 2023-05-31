import { Component } from '@angular/core';

@Component({
  selector: 'app-driver-inbox',
  templateUrl: './driver-inbox.component.html',
  styleUrls: ['./driver-inbox.component.css']
})
export class DriverInboxComponent {
  
  showRideId: number = -1;
  ngOnInit() {
    console.log(history.state.data.rideId);
    this.showRideId = history.state.data.rideId;
  }
}
