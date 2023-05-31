import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { DriverService } from 'src/app/services/driver.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private http: HttpClient, private router: Router, private driverService: DriverService) { }

  login(user: object): Observable<object> {
    return this.http.post<object>(environment.apiHost + 'user/login', user);
  }

  logout() {
    if (localStorage.getItem('userRole') === "ROLE_DRIVER") {
      let workingHourId = localStorage.getItem('workingHourId')??"";
      if (workingHourId !== "") {
        this.driverService.endWorkingHour(workingHourId).subscribe(request => {
          localStorage.removeItem('workingHourId');
        });
      }
    }
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('userRole');
    localStorage.removeItem('driverStatus');
    this.router.navigate(['login']);
  }
}
