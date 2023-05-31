import { Component, ViewChild } from '@angular/core';
import { UpdateUserDTO, UserFull } from 'src/app/entities/user';
import { PassengerService } from 'src/app/services/passenger.service';

@Component({
  selector: 'app-passenger-account',
  templateUrl: './passenger-account.component.html',
  styleUrls: ['./passenger-account.component.css']
})
export class PassengerAccountComponent {

  constructor(private passengerService: PassengerService) {
    this.passengerService.get(Number.parseInt(localStorage.getItem('userId') ?? '')).subscribe((res) => {
      console.log(res);
      this.PassengerData = res;
    });
  }

  PassengerData: UserFull = {
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
    this.NewProfilePic = base64;
  }

  saveChanges(data : [number, UpdateUserDTO]) {
    this.passengerService.updatePassenger(data[0], data[1])
      .subscribe((res: any) => {
        window.location.reload();
      });
  }
}
