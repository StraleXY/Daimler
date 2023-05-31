import { Component, Input } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { AdminServiceService } from 'src/app/services/admin-service.service';

interface VehicleType {
    value: number,
    viewValue: string
}

interface Driver {
    name?: string | null,
    surname?: string | null,
    profilePicture?: string | null,
    telephoneNumber?: string | null,
    email?: string | null,
    address?: string | null,
    password?: string | null
}

interface Vehicle {
    vehicleType?: string | null
    model?: string | null
    licenseNumber?: string | null
    currentLocation?: any,
    passengerSeats?: number | null,
    babyTransport?: boolean | null,
    petTransport?: boolean | null
}

@Component({
  selector: 'app-admin-driver-register',
  templateUrl: './admin-driver-register.component.html',
  styleUrls: ['./admin-driver-register.component.css']
})

export class AdminDriverRegisterComponent {

  constructor(private santizier : DomSanitizer,
              private adminService: AdminServiceService) {}

  hidePassword: Boolean = true;
  hideConfirmPassword: Boolean = true;

  driverForm = new FormGroup({
      name: new FormControl('', [Validators.required]),
      surname: new FormControl('', [Validators.required]),
      phone: new FormControl('', [Validators.required, Validators.pattern("^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{3,6}$")]),
      email: new FormControl('', [Validators.required, Validators.email]),
      address: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required, Validators.pattern("^.{8,}$"), this.matchValidator("confirmPassword", true)]),
      confirmPassword: new FormControl('', [Validators.required, Validators.pattern("^.{8,}$"), this.matchValidator("password")]),
  });

  vehicleForm = new FormGroup({
      vehicleType: new FormControl('', [Validators.required]),
      model: new FormControl('', [Validators.required]),
      licenseNumber: new FormControl('', [Validators.required]),
      numberOfSeats: new FormControl(1, [Validators.required, Validators.min(1), Validators.max(10)]),
      hasBaby: new FormControl(false, []),
      hasPet: new FormControl(false, []),
  });

  vehicleTypes: VehicleType[] = [
      {value: 0, viewValue: 'Limousine'},
      {value: 1, viewValue: 'Coupe'},
      {value: 2, viewValue: 'SUV'},
  ];


  private _profilePic: string = "";
  @Input() set profilePic(value: string) {
    this._profilePic = value;
    if(value == "") this.selectedProfilePicUrl = "./assets/img_avatar.png";
    else this.selectedProfilePicUrl = this.santizier.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + this._profilePic);
  }
  pickedPicture: string = "";

  selectedProfilePic: any = null;
  selectedProfilePicUrl : SafeUrl = "./assets/img_avatar.png";

  onFileSelected(event: any): void {
      this.selectedProfilePic = event.target.files[0] ?? null;

      var reader = new FileReader();
      reader.onload = this._handleReaderLoaded.bind(this);
      reader.readAsBinaryString(this.selectedProfilePic);

      this.selectedProfilePicUrl = this.santizier.bypassSecurityTrustUrl(window.URL.createObjectURL(this.selectedProfilePic));
  }

  _handleReaderLoaded(readerEvt : any) {
    var binaryString = readerEvt.target.result;
    this.pickedPicture = btoa(binaryString);
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

  prevent(e: Event) : void {
      e.preventDefault();
  }

  touchAll() : void {
      //TODO: zasto nece for (var i in driverForm) -> i is type any/string
      //      ne dozvoljava controls[i]
      this.driverForm.controls['name'].markAsTouched();
      this.driverForm.controls['surname'].markAsTouched();
      this.driverForm.controls['phone'].markAsTouched();
      this.driverForm.controls['email'].markAsTouched();
      this.driverForm.controls['address'].markAsTouched();
      this.driverForm.controls['password'].markAsTouched();
      this.driverForm.controls['confirmPassword'].markAsTouched();
      this.vehicleForm.controls['vehicleType'].markAsTouched();
      this.vehicleForm.controls['model'].markAsTouched();
      this.vehicleForm.controls['licenseNumber'].markAsTouched();
  }

  save(e: Event) : void {
      e.preventDefault();
      if (!this.driverForm.valid || !this.vehicleForm.valid) {
          this.touchAll();
          return
      }
      let dform = this.driverForm.value;
      let driver: Driver = {}
      driver.name = dform.name;
      driver.surname = dform.surname;
      driver.email = dform.email;
      driver.address = dform.address;
      driver.telephoneNumber = dform.phone;
      driver.profilePicture = this.pickedPicture;
      driver.password = dform.password;

      let vform = this.vehicleForm.value;
      let vehicle: Vehicle = {};
      vehicle.model = vform.model;
      // TODO: napraviti neku mapu nesto
      vehicle.vehicleType = "Coupe";
      vehicle.licenseNumber = vform.licenseNumber;
      vehicle.passengerSeats = vform.numberOfSeats;
      vehicle.babyTransport = vform.hasBaby;
      vehicle.petTransport = vform.hasPet;
      // TODO: currentLocation ne mora biti ovo defaultno
      //       za sta ce nam uopste??
      vehicle.currentLocation = {
          "address": "Bulevar oslobodjenja 46",
          "latitude": 45.267136,
          "longitude": 19.833549
      }

      this.adminService.registerDriver(driver)
      .subscribe((res: any) => {
          this.adminService.registerVehicle(res.id, vehicle)
          .subscribe((_: any) => {
            window.location.reload();
          });
      });
  }
}
