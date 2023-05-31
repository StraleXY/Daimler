import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { UserFull } from 'src/app/entities/user';
import { RideService } from 'src/app/services/ride.service';
import { InvitationDTO } from 'src/app/entities/rides';

export interface LinkedPassenger {
    id: number;
    email: string;
    responded: boolean;
    accepted: boolean;
    color: string;
}

@Component({
  selector: 'app-linked-ride-persons',
  templateUrl: './linked-ride-persons.component.html',
  styleUrls: ['./linked-ride-persons.component.css']
})
export class LinkedRidePersonsComponent {

  linkedPassengersForm = new FormGroup({
    linkedInput: new FormControl()
  });

  lastItemId: number = 0;

  constructor(private rideService: RideService) { }

  @Output() next: EventEmitter<void> = new EventEmitter();
  @Output() passengerList: EventEmitter<LinkedPassenger[]> = new EventEmitter();
  @Input() inviterId: number = 0;
  @Input() addressFrom: string = "";
  @Input() addressTo: string = "";

  linkedPassengerList: LinkedPassenger[] = []


  public response(email: string, accepted: string, id: string) {
    for (let passenger of this.linkedPassengerList) {
        if (passenger.email == email) {
            passenger.id = Number(id)
            passenger.responded = true;
            passenger.accepted = accepted === 'true' ? true : false;
            if (accepted === 'true')
                passenger.color = 'green';
            else
                passenger.color = 'red';
            console.log(accepted);
            console.log(passenger.color);
            break;
        }
    }
  }

  submit() {
    let email = "";
    if (!this.linkedPassengersForm.valid) return;
    if (this.linkedPassengersForm.get("linkedInput")?.value)
      email = this.linkedPassengersForm.get("linkedInput")?.value;
    for (let passenger of this.linkedPassengerList) {
        if (passenger.email == email) {
            alert('You cannot invite the same person again')
            this.linkedPassengersForm.get("linkedInput")?.setValue("");
            break;
        }
    }
    let req: InvitationDTO = {
      "invitedEmail": email,
      "inviterId": this.inviterId,
      "addressFrom": this.addressFrom,
      "addressTo": this.addressTo
    };
    console.log(req);
    this.rideService.invite(req)
      .subscribe((res: any) => {
        this.linkedPassengersForm.get("linkedInput")?.setValue("");
        this.linkedPassengerList.push({email: email, responded: false, accepted:false, color: 'black', id:0});
      });
  }

  confirmClick(): void {
      this.passengerList.emit(this.linkedPassengerList);
      this.next.emit();
  }
}
