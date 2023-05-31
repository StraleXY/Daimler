import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators, ValidationErrors, ValidatorFn, AbstractControl } from '@angular/forms';
import { RegisterService } from 'src/app/services/register-service.service'


export interface User {
    name? : string | null;
    surname? : string | null;
    profilePicture? : string | null;
    telephoneNumber? : string | null;
    email? : string | null;
    address? : string | null;
    password? : string | null;
}

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css']
})

export class RegisterComponent {
    showForm: Boolean = true;
    hidePassword: Boolean = true;
    hideConfirmPassword: Boolean = true;

    createUserForm = new FormGroup({
        name: new FormControl('', [Validators.required]),
        surname: new FormControl('', [Validators.required]),
        phone: new FormControl('', [Validators.required, Validators.pattern("^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{3,6}$")]),
        email: new FormControl('', [Validators.required, Validators.email]),
        address: new FormControl('', [Validators.required]),
        password: new FormControl('', [Validators.required, Validators.pattern("^.{8,}$"), this.matchValidator("confirmPassword", true)]),
        confirmPassword: new FormControl('', [Validators.required, Validators.pattern("^.{8,}$"), this.matchValidator("password")]),
    });

    constructor(private registerService: RegisterService) {
    }

    prevent(e: Event): void {
        e.preventDefault();
    }

    submit(e: Event): void {
        e.preventDefault();
        if (this.createUserForm.valid) {
            let form = this.createUserForm.value;
            let user: User = {};

            user.name = form.name;
            user.surname = form.surname;
            user.profilePicture = "";
            user.telephoneNumber = form.phone;
            user.email = form.email;
            user.address = form.address;
            user.password = form.password;

            this.registerService
                .registerUser(user)
                .subscribe((res: any) => {
                    console.log(res);
                    this.showForm = false;
            });
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
