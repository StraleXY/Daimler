import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-passenger-inbox',
  templateUrl: './passenger-inbox.component.html',
  styleUrls: ['./passenger-inbox.component.css']
})
export class PassengerInboxComponent implements OnInit {

  showRideId: number = -1;
  ngOnInit() {
    console.log(history.state.data.rideId);
    this.showRideId = history.state.data.rideId;
  }
}
