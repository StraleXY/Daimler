import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators, ValidationErrors, ValidatorFn, AbstractControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent {
  token: string = "";

  showForm: Boolean = true;
  hidePassword: Boolean = true;
  hideConfirmPassword: Boolean = true;

  changePasswordForm = new FormGroup({
    password: new FormControl('', [Validators.required, Validators.pattern("^.{8,}$"), this.matchValidator("confirmPassword", true)]),
    confirmPassword: new FormControl('', [Validators.required, Validators.pattern("^.{8,}$"), this.matchValidator("password")]),
  });

  constructor(private route: ActivatedRoute, private http: HttpClient) {}

  ngOnInit() {
      this.route.queryParams
      .subscribe(params => {
          this.token = params['token'];
          console.log(this.token);
      });
  }

  prevent(e: Event): void {
    e.preventDefault();
  }

  submit(e: Event): void {
    e.preventDefault();
    if (this.changePasswordForm.valid) {
      let form = this.changePasswordForm.value;
      let password = form.password??'';
      if (password !== '') {
        let body = new HttpParams();
        body = body.set("newPassword", password??'');
        this.http.post(environment.apiHost + 'user/recover/' + this.token, body)
          .subscribe((res: any) => {
            console.log(res);
            this.showForm = false;
          }, (error: HttpErrorResponse) => {
            console.log(error);
          });
      }
    }
  }

  matchValidator( matchTo: string, reverse?: boolean): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.parent && reverse) {
        const c = (control.parent?.controls as any)[matchTo] as AbstractControl;
        if (c) { c.updateValueAndValidity(); }
        return null;
      }
      return !!control.parent && !!control.parent.value && control.value === (control.parent?.controls as any)[matchTo].value
        ? null : { matching: true };
    };
  }
}
