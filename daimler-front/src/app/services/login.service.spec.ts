import { DatePipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { fakeAsync, flush, TestBed, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { Location } from '@angular/common';

import { LoginService } from './login.service';
import { HomeComponent } from '../home/home.component';
import { LoginComponent } from '../login/login.component';
import { DriverService } from './driver.service';
import { Observable, of } from 'rxjs';

describe('LoginService', () => {
  let loginService: LoginService;
  let location: Location;
  let router: Router;
  let driverService: DriverService;

  beforeEach(() => {
    const spyDriverService = jasmine.createSpyObj<DriverService>(['endWorkingHour']);
    spyDriverService.endWorkingHour.and.returnValue(of({}));

    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          {path:'', component: HomeComponent},
          {path:'login', component: LoginComponent}
        ])
      ],
      providers: [
        DatePipe,
        {provide: DriverService, useValue: spyDriverService}
      ]
    });
    loginService = TestBed.inject(LoginService);
    driverService = TestBed.inject(DriverService);
    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    router.initialNavigation();
  });

  it('should be created', () => {
    expect(loginService).toBeTruthy();
  });

  it('should clear localStorage and redirect to login', fakeAsync(() => {
    localStorage.setItem('token', 'token');
    localStorage.setItem('userId', '1');
    localStorage.setItem('userRole', 'ROLE_ADMIN');
    loginService.logout();
    tick();
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('userId')).toBeNull();
    expect(localStorage.getItem('userRole')).toBeNull();
    expect(location.path()).toBe('/login');
  }));

  it('should end working hour', fakeAsync(() => {
    localStorage.setItem('userRole', 'ROLE_DRIVER');
    localStorage.setItem('workingHourId', '2');
    localStorage.setItem('token', 'token');
    localStorage.setItem('userId', '3');
    loginService.logout();
    flush();
    expect(driverService.endWorkingHour).toHaveBeenCalledTimes(1);
    expect(localStorage.getItem('workingHourId')).toBeNull();
  }));
});
