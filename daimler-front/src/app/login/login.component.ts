import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators, ValidationErrors, ValidatorFn, AbstractControl } from '@angular/forms';
import { LoginService } from 'src/app/services/login.service';
import { Router } from '@angular/router';
import { DriverService } from 'src/app/services/driver.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  hidePassword: Boolean = true;
  loginError: string = '';

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.pattern("^.{8,}$")]),
  });

  constructor(private loginService: LoginService, private router: Router, private driverService: DriverService) {
  }

  prevent(e: Event): void {
    e.preventDefault();
  }

  submit(e: Event): void {
    e.preventDefault();
    if (this.loginForm.valid) {
      let form = this.loginForm.value;
      let user: any = {};

      user.email = form.email;
      user.password = form.password;

      this.loginService
        .login(user)
        .subscribe({
          next: res => this.processLogin(res),
          error: err => alert("Credentials incorrect."),

        });
    }
  }

  processLogin(res: any) {
    localStorage.setItem('token', res.accessToken);
    localStorage.setItem('userId', res.userId);
    localStorage.setItem('userRole', res.userRole);
    console.log(res);
    if (res.userRole == "ROLE_DRIVER") {
      localStorage.setItem('driverStatus', 'active');
      this.driverService.startWorkingHour(res.userId).subscribe(response => {
        localStorage.setItem('workingHourId', response.id);
      });
      this.router.navigate(['driver/home']);
    } else if (res.userRole == "ROLE_PASSENGER") {
      this.router.navigate(['passenger/home']);
    } else if (res.userRole == "ROLE_ADMIN") {
      this.router.navigate(['admin/home']);
    }
  }
}
