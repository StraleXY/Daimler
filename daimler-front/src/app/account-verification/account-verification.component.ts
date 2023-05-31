import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RegisterService } from 'src/app/services/register-service.service'

@Component({
  selector: 'app-account-verification',
  templateUrl: './account-verification.component.html',
  styleUrls: ['./account-verification.component.css']
})

export class AccountVerificationComponent {

    success: Boolean = false;
    token: string = "";

    constructor(private route: ActivatedRoute,
               private registerService: RegisterService) {}

    ngOnInit() {
        this.route.queryParams
        .subscribe(params => {
            this.token = params['token'];
            console.log(this.token);
            this.registerService
                .validateUser(this.token)
                .subscribe((res: any) => {
                    console.log(res);
                    this.success = true;
            });
        });
    }
}
