import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RideDTO } from 'src/app/entities/rides';
import { UserFull } from 'src/app/entities/user';
import { VehicleDTO } from 'src/app/entities/vehicle';
import { DriverService } from 'src/app/services/driver.service';
import { RideService } from 'src/app/services/ride.service';

@Component({
  selector: 'app-ride-in-progress-passenger',
  templateUrl: './ride-in-progress-passenger.component.html',
  styleUrls: ['./ride-in-progress-passenger.component.css']
})
export class RideInProgressPassengerComponent {
  constructor(private driverService : DriverService, private rideService : RideService) { }
  
  @Output() end: EventEmitter<number> = new EventEmitter<number>();
  @Input() set ride(value : RideDTO) {
    if (value == undefined) return;
    this.driverService.get(value.driver.id).subscribe(driver => {
      this.driver = driver;
    });
    this.driverService.getVehicle(value.driver.id).subscribe(vehicle => {
      this.vehicle = vehicle;      
    });
    this._ride = value;
  }

  estimatedTime: number = 7;

  public _ride : RideDTO = {
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

  public driver : UserFull = {
    id: 1,
    name: '',
    surname: '',
    profilePicture: '',
    telephoneNumber: '',
    email: '',
    address: ''
  }

  public vehicle : VehicleDTO = {
    model: '',
    licenseNumber: '',
    vehicleType: '',
    passengerSeats: 0,
    babyTransport: false,
    petTransport: false,
    currentLocation: {
      id: 0,
      address: '',
      latitude: 0,
      longitude: 0
    },
    driverId: 0,
    id: 0
  }

  panicClick() {
    this._ride.status = "PANIC";
    this.rideService.panic(this._ride.id, "Panika me hvata!").subscribe(res => {
      alert("RIDE FINISHED WITH PANIC!");
      this.end.emit(this._ride.id);
    })
  }
}
