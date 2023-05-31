import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

export interface BabyPets {
    baby: boolean,
    pets: boolean,
    isFavourite: boolean,
    scheduled: number
}

@Component({
  selector: 'app-more-ride-settings',
  templateUrl: './more-ride-settings.component.html',
  styleUrls: ['./more-ride-settings.component.css']
})
export class MoreRideSettingsComponent {

  babyPetsForm = new FormGroup({
    hasBaby: new FormControl(),
    hasPet: new FormControl(),
    isFavourite: new FormControl(),
  });

  @Output() onNextClick : EventEmitter<BabyPets> = new EventEmitter();
  babyPets: BabyPets = {baby: false, pets: false, isFavourite: false, scheduled: Date.now()};

  constructor() {
    this.setTime();
  }

  setTime() {
    let date = new Date();
    this.minutes = String(date.getMinutes());
    this.hours = String(date.getHours());
    this.timeSelected();
  }

  submit(e: Event) {
      this.babyPets.baby = this.babyPetsForm.get("hasBaby")?.value;
      this.babyPets.pets = this.babyPetsForm.get("hasPet")?.value;
      this.babyPets.isFavourite = this.babyPetsForm.get("isFavourite")?.value;
      this.onNextClick.emit(this.babyPets);
  }

  hours: string = '00';
  minutes: string = '00';

  timeSelected() {
    if(Number(this.hours) >= 24) this.hours = '23';
    if(Number(this.hours) < 0) this.hours = '00';
    if(Number(this.hours) < 10) this.hours = '0' + Number(this.hours);

    if(Number(this.minutes) >= 60) this.minutes = '59';
    if(Number(this.minutes) < 0) this.minutes = '00';
    if(Number(this.minutes) < 10) this.minutes = '0' + Number(this.minutes);

    let pickedDate = new Date();
    pickedDate.setHours(Number(this.hours));
    pickedDate.setMinutes(Number(this.minutes));
    console.log(pickedDate.getTime());

    let diff = pickedDate.getTime() - Date.now()
    console.log(diff / 3600000);

    if(diff < 0 || diff / 3600000 > 5) this.setTime();
    this.babyPets.scheduled = pickedDate.getTime();
    
  }

  addHours() {
    this.hours = String(Number(this.hours) + 1);
    this.timeSelected();
  }
  addMinutes() {
    this.minutes = String(Number(this.minutes) + 1);
    this.timeSelected();

  }
  removeHours() {
    this.hours = String(Number(this.hours) - 1);
    this.timeSelected();
  }
  removeMinutes() {
    this.minutes = String(Number(this.minutes) - 1);
    this.timeSelected();
  }
}
