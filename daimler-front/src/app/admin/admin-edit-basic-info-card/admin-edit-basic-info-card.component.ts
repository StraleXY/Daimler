import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { Admin, UpdateAdminDTO } from 'src/app/entities/admin';
import { AdminServiceService } from 'src/app/services/admin-service.service';

@Component({
  selector: 'app-admin-edit-basic-info-card',
  templateUrl: './admin-edit-basic-info-card.component.html',
  styleUrls: ['./admin-edit-basic-info-card.component.css']
})
export class AdminEditBasicInfoCardComponent {

  constructor(private adminService : AdminServiceService) {}

  hidePassword: Boolean = true;
  hideConfirmPassword: Boolean = true;
  
  @Input() basicInfoDTO : Admin = {
    id: 0,
    name: '',
    surname: '',
    email: ''
  }

  editUserForm = new FormGroup({
    name: new FormControl(this.basicInfoDTO.name, [Validators.required]),
    surname: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
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

      let user : UpdateAdminDTO = {
        name: String(form.name),
        surname: String(form.surname),
        email: String(form.email),
        password: String(form.password)
      };

      this.adminService.updateAdmin(this.basicInfoDTO.id, user)
      .subscribe((res: any) => {
        window.location.reload();
      });
      
    }
  }

  @Output() editInfo = new EventEmitter<boolean>();

  toggleEditInfo() {
    this.editInfo.emit(false);
  }
  
}
