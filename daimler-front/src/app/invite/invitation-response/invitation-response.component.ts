import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { InvitationResponseDTO } from 'src/app/entities/rides';
import { RideService } from 'src/app/services/ride.service';

@Component({
  selector: 'app-invitation-response',
  templateUrl: './invitation-response.component.html',
  styleUrls: ['./invitation-response.component.css']
})
export class InvitationResponseComponent {
    id: number = 0;
    email: string = "";
    passengerId: number = 0;
    accepted: boolean = false;

    constructor(private route: ActivatedRoute,
               private rideService: RideService) {}

    ngOnInit() {
        this.route.queryParams
        .subscribe(params => {
            this.id = params['id'];
            this.email = params['email'];
            this.passengerId = params['passengerId'];
            this.accepted = params['accepted'];
            let req: InvitationResponseDTO = {
                inviterId: this.id,
                invitedEmail: this.email,
                invitedId: this.passengerId,
                accepted: this.accepted
            };
            console.log(req);
            this.rideService.invitationResponse(req)
                .subscribe((res: any) => {
            });
        });
    }

    ngOnChanges() {
    }
}
