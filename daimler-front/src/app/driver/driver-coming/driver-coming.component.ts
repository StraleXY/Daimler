import { Component, Input } from '@angular/core';
import { RideDTO } from 'src/app/entities/rides';
import { UserFull } from 'src/app/entities/user';
import { VehicleDTO } from 'src/app/entities/vehicle';
import { DriverService } from 'src/app/services/driver.service';

@Component({
  selector: 'app-driver-coming',
  templateUrl: './driver-coming.component.html',
  styleUrls: ['./driver-coming.component.css']
})
export class DriverComingComponent {

  constructor(private driverService : DriverService) { }
  
  @Input() set ride(value : RideDTO) {
    if (value == undefined) return;
    this.driverService.get(value.driver.id).subscribe(driver => {
      this.driver = driver;
    });
    this.driverService.getVehicle(value.driver.id).subscribe(vehicle => {
      this.vehicle = vehicle;      
    });
    this.estimatedTime = value.estimatedTimeInMinutes;
  }

  estimatedTime: number = 7;

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
}
