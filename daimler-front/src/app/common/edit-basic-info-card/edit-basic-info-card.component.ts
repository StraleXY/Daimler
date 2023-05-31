import { BooleanInput } from '@angular/cdk/coercion';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { Router } from '@angular/router';
import { UserFull, UpdateUserDTO } from 'src/app/entities/user';
import { User } from 'src/app/register/register.component';
import { PassengerService } from 'src/app/services/passenger.service';

@Component({
  selector: 'app-edit-basic-info-card',
  templateUrl: './edit-basic-info-card.component.html',
  styleUrls: ['./edit-basic-info-card.component.css']
})
export class EditBasicInfoCardComponent {

  constructor( private router: Router, private passengerService : PassengerService) {}

  hidePassword: Boolean = true;
  hideConfirmPassword: Boolean = true;
  
  @Input() basicInfoDTO: UserFull = {
    id: 0,
    name: '',
    surname: '',
    email: '',
    telephoneNumber: '',
    address: '',
    profilePicture: ''
  };
  @Input() newProfilePic: string = "";

  editUserForm = new FormGroup({
    name: new FormControl(this.basicInfoDTO.name, [Validators.required]),
    surname: new FormControl('', [Validators.required]),
    phone: new FormControl('', [Validators.required, Validators.pattern("^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{3,6}$")]),
    email: new FormControl('', [Validators.required, Validators.email]),
    address: new FormControl('', [Validators.required]),
    password: new FormControl({value: '', disabled: true}, [Validators.required, Validators.pattern("^.{8,}$"), this.matchValidator("confirmPassword", true)]),
    confirmPassword: new FormControl({value: '', disabled: true}, [Validators.required, Validators.pattern("^.{8,}$"), this.matchValidator("password")]),
  });

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

  changePasswordCheckChanged(checked: MatCheckboxChange) {
    if(checked.checked){
      this.editUserForm.get('password')?.enable();
      this.editUserForm.get('confirmPassword')?.enable();
    } else {
      this.editUserForm.get('password')?.disable();
      this.editUserForm.get('password')?.setValue("");
      this.editUserForm.get('confirmPassword')?.disable();
      this.editUserForm.get('confirmPassword')?.setValue("");
    }
  }

  update(e: Event): void {
    e.preventDefault();
    if (this.editUserForm.valid) {
      let form = this.editUserForm.value;
      
      if(form.password != form.confirmPassword) return;

      let user : UpdateUserDTO = {
        name: String(form.name),
        surname: String(form.surname),
        profilePicture: this.newProfilePic == "" ? this.basicInfoDTO.profilePicture : this.newProfilePic,
        telephoneNumber: String(form.phone),
        email: String(form.email),
        address: String(form.address),
        password: String(form.password)
      };
      this.saveInfo.emit([this.basicInfoDTO.id, user])
      
    }
  }

  @Input() hasTitle = false;
  @Output() editInfo = new EventEmitter<boolean>();
  @Output() saveInfo = new EventEmitter<[number, UpdateUserDTO]>();

  toggleEditInfo() {
    this.editInfo.emit(false);
  }

}