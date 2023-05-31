import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-inbox',
  templateUrl: './admin-inbox.component.html',
  styleUrls: ['./admin-inbox.component.css']
})
export class AdminInboxComponent {
  
  showRideId: number = -1;
  ngOnInit() {
    console.log(history.state.data.rideId);
    this.showRideId = history.state.data.rideId;
  }
}
