import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators, ValidationErrors, ValidatorFn, AbstractControl } from '@angular/forms';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-account-recovery',
  templateUrl: './account-recovery.component.html',
  styleUrls: ['./account-recovery.component.css']
})
export class AccountRecoveryComponent {
  showForm: boolean = true;

  accountRecoveryForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
  });

  constructor(private http: HttpClient) {
  }

  submit(e: Event): void {
    e.preventDefault();
    let body = new HttpParams();
    body = body.set("email", this.accountRecoveryForm.value.email??'');
    this.http.post(environment.apiHost + 'user/forgotPassword', body)
      .subscribe((res: any) => {
        console.log(res);
        this.showForm = false;
      }, (error: HttpErrorResponse) => {
        console.log(error);
        alert("Invalid email");
      });
  }
}
