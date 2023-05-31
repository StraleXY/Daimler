import { Component } from '@angular/core';
import { UpdateUserDTO, UserFull } from 'src/app/entities/user';
import { DriverService } from 'src/app/services/driver.service';

@Component({
  selector: 'app-driver-account',
  templateUrl: './driver-account.component.html',
  styleUrls: ['./driver-account.component.css']
})
export class DriverAccountComponent {
  constructor(private driverService: DriverService) {
    let userId : string = localStorage.getItem('userId') ?? '';
    this.driverService.get(parseInt(userId)).subscribe((res) => {
      console.log(res);
      this.DriverData = res;
    });
  }

  DriverData: UserFull = {
    id: 0,
    name: '',
    surname: '',
    profilePicture: '',
    telephoneNumber: '',
    email: '',
    address: ''
  };
  NewProfilePic: string = "";
  EditData: boolean = false;

  showEditInfo(visible : boolean) {
    this.EditData = visible;
  }

  profilePicChanged(base64 : string) {
    console.log(base64);
    this.NewProfilePic = base64;
  }

  saveChanges(data : [number, UpdateUserDTO]) {
    this.driverService.requestUpdate(data[0], data[1])
      .subscribe((res: any) => {
        console.log(res);
        window.location.reload();
      });
  }
}
