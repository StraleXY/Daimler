import { Component, Input } from '@angular/core';
import { DepartureDestinationDTO, LocationDTO, RideDTO } from 'src/app/entities/rides';

@Component({
  selector: 'app-scheduled-ride',
  templateUrl: './scheduled-ride.component.html',
  styleUrls: ['./scheduled-ride.component.css']
})
export class ScheduledRideComponent {

  @Input() ride : RideDTO = {
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

  getString() : string {
    let time = new Date();
    time.setTime(this.ride.scheduledTimestamp);
    return this.formatTime(time.getHours()) + ":" + this.formatTime(time.getMinutes());
  }

  formatTime(time: number) : string {
    if (time < 9) return '0' + time;
    return String(time);
  }

  public getLocationList(locations : DepartureDestinationDTO[]) : LocationDTO[] {
    let loc : LocationDTO[] = [];
    for(let i = 0; i < locations.length; i++) {
        loc.push(locations[i].departure);
        if(i == locations.length - 1) loc.push(locations[i].destination);
    }
    return loc;
  }
}
