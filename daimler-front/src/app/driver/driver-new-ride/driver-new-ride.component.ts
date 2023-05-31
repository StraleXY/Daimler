import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RideDTO } from 'src/app/entities/rides';

@Component({
  selector: 'app-driver-new-ride',
  templateUrl: './driver-new-ride.component.html',
  styleUrls: ['./driver-new-ride.component.css']
})
export class DriverNewRideComponent {

  @Output() reject: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() start: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() end: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() panic: EventEmitter<boolean> = new EventEmitter<boolean>();

  isStartVisible : boolean = true;
  isRejectVisible : boolean = true;
  isEndVisible : boolean = true;
  isPanicVisible : boolean = true;

  rideName : string = 'New ride';

  @Input() set _ride(value: RideDTO) {
    this.ride = value;
    this.handleButtons(value.status);
  }

  public ride : RideDTO = {
    id: 0,
    startTime: '',
    endTime: '',
    totalCost: 384,
    driver: {
      id: 0,
        email: '',
        name: '',
        surname: '',
        profilePicture: ''
    },
    passengers: [
      {
        id: 8,
        name: 'Strahinja',
        surname: 'Sekulic',
        email: 'strahinja0123@gmail.com',
        profilePicture: ''
      },
      {
        id: 6,
        name: 'Lazar',
        surname: 'Milanovic',
        email: 'milanovic.sv15.2020@uns.ac.rs',
        profilePicture: ''
      }
    ],
    estimatedTimeInMinutes: 17,
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

  public lastItemId : number = this.ride.passengers[this.ride.passengers.length-1].id;

  public rejectClick() {
      this.reject.emit(true);
  }

  public startClick() {
      this.start.emit(true);
      this.handleButtons("ACTIVE");
  }

  public endClick() {
      this.end.emit(true);
  }

  public panicClick() {
    this.panic.emit(true);
  }

  private handleButtons(status: string) {
    if(status == "ACTIVE") {
      this.isStartVisible = false;
      this.isRejectVisible = false;
      this.isEndVisible = true;
      this.isPanicVisible = true;
      this.rideName = "Ongoing";
    }
    else if(status == "ACCEPTED" || status == "PENDING") {
      this.isStartVisible = true;
      this.isRejectVisible = true;
      this.isEndVisible = false;
      this.isPanicVisible = false;
      this.rideName = "New ride";
    }
  }

}
