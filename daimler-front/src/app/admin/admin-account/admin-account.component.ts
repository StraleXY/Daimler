import { Component } from '@angular/core';
import { Admin } from 'src/app/entities/admin';
import { UserFull } from 'src/app/entities/user';
import { AdminServiceService } from 'src/app/services/admin-service.service';
import { DriverService } from 'src/app/services/driver.service';

@Component({
  selector: 'app-admin-account',
  templateUrl: './admin-account.component.html',
  styleUrls: ['./admin-account.component.css']
})
export class AdminAccountComponent {

  constructor(private adminService: AdminServiceService, private driverService : DriverService) {
    this.adminService.getAdmin(Number.parseInt(localStorage.getItem('userId') ?? '')).subscribe((res) => {
      console.log(res);
      this.AdminData = res;
    })

    this.driverService.getRequests().subscribe((res) => {
      console.log(res);
      this.driverRequests = res;
    })
  }

  AdminData: Admin = {
    id: 0,
    name: '',
    surname: '',
    email: ''
  }

  driverRequests : UserFull[] = [];

  EditData: boolean = false;
  showEditInfo(visible : boolean) {
    this.EditData = visible;
  }

}
