import { Component, Input } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { UpdateRequest, UserFull } from 'src/app/entities/user';
import { AdminServiceService } from 'src/app/services/admin-service.service';
import { DriverService } from 'src/app/services/driver.service';

@Component({
  selector: 'app-driver-update-requests',
  templateUrl: './driver-update-requests.component.html',
  styleUrls: ['./driver-update-requests.component.css']
})
export class DriverUpdateRequestsComponent {
  
  constructor(private driverService : DriverService, private adminService : AdminServiceService, private santizier : DomSanitizer) {}

  count : number = 0;
  noData : boolean = true;
  isSingleItem : boolean = true;

  setCount(value : number) {
    this.count = value;
    this.noData = this.count == 0;
    this.isSingleItem = this.count == 1;
  }

  _driverRequests : UpdateRequest[] = [];

  @Input() set driverRequests(requests : UserFull[]) {
    requests.forEach(updated => {
      this.setCount(this.count + 1);
      this.driverService.get(updated.id).subscribe((old) => this._driverRequests.push({old : old, updated : updated}))
    });
  }

  deny(request : UpdateRequest) {
    this.driverService.deleteRequest(request.old.id).subscribe(_ => {
      this.removeRequestView(request);
    });
  }

  approve(request : UpdateRequest) {
    this.adminService.approveDriverRequest(request.updated.id, {
      name: request.updated.name,
      surname: request.updated.surname,
      profilePicture: request.updated.profilePicture,
      telephoneNumber: request.updated.telephoneNumber,
      email: request.updated.email,
      address: request.updated.address,
      password: '',
    }).subscribe(_ => {
      this.removeRequestView(request);
    });
  }

  removeRequestView(request : UpdateRequest) {
    this._driverRequests = this._driverRequests.filter(item => item != request);
    this.setCount(this.count - 1);
  }
}
