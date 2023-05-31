import { trigger, transition, style, animate } from '@angular/animations';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { UnregisteredService } from 'src/app/services/unregistered.service'

interface CarType {
    value: string;
    viewValue: string;
}

// WAYPOINT INTERFACE
export interface LatLon {
    address: string | null | undefined
    latitude: number;
    longitude: number;
}

export interface EstimationDTO {
    estimatedTimeInMinutes: number;
    estimatedCost: number;
    distance: number;
}

@Component({
  selector: 'app-get-a-ride',
  templateUrl: './get-a-ride.component.html',
  styleUrls: ['./get-a-ride.component.css'],
  animations: [
    trigger('fadeSlideInOut', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(10px)' }),
        animate('500ms', style({ opacity: 1, transform: 'translateY(0)' })),
      ]),
      transition(':leave', [
        animate('500ms', style({ opacity: 0, transform: 'translateY(10px)' })),
      ]),
    ])
  ]
})

export class GetARideComponent {

    constructor(private unregisteredService: UnregisteredService) {}

    getARide = new FormGroup({
        carType: new FormControl('', [Validators.required]),
        pickupAddress: new FormControl('', [Validators.required]),
        destinationAddress: new FormControl('', [Validators.required]),
    });

    cars: CarType[] = [
        {value: 'limousine', viewValue: 'Limousine'},
        {value: 'coupe', viewValue: 'Coupe'},
        {value: 'suv', viewValue: 'SUV'}
    ];
    @Input() firstInput: string="";
    @Input() secondInput: string="";

    ngOnChanges(): void{
        if (this.firstInput != "")
            this.getARide.controls['pickupAddress'].setValue(this.firstInput);
        if (this.secondInput != "")
            this.getARide.controls['destinationAddress'].setValue(this.secondInput);
    }

    @Output() isToggled  = new EventEmitter<boolean>();
    @Output() estimation = new EventEmitter<EstimationDTO>();
    @Output() waypoints = new EventEmitter<any>();
    @Output() vehicleType = new EventEmitter<CarType>();

    submit(e: Event) : void {
        // this.isToggled.emit(true);
        // TODO: Refactor this
        if (this.getARide.valid) {
            this.firstInput = "";
            this.secondInput = "";
            const numberRegex = /[0-9]+[a-z]{0,1}/;
            const addressRegex = /[a-zA-Z\s]+/;
            let fNum: string, sNum: string;
            let fAdd: string, sAdd: string;
            let form = this.getARide.value;
            let first: LatLon = {address:form.pickupAddress, longitude:0, latitude:0}
            let second: LatLon = {address:form.destinationAddress, longitude:0, latitude:0}
            let firstNumber = form.pickupAddress?.match(numberRegex);
            let firstAddress = form.pickupAddress?.match(addressRegex);
            let secondNumber = form.destinationAddress?.match(numberRegex);
            let secondAddress = form.destinationAddress?.match(addressRegex);
            if (firstNumber != null && firstAddress != null && secondNumber != null && secondAddress != null) {
                fNum = firstNumber[0];
                sNum = secondNumber[0];
                fAdd = firstAddress[0].replaceAll(" ", "+");
                sAdd = secondAddress[0].replaceAll(" ", "+");
                this.unregisteredService.geoDecode(fNum, fAdd)
                .subscribe((res: any) => {
                    first.longitude = res[0].lon;
                    first.latitude = res[0].lat;
                    this.unregisteredService.geoDecode(sNum, sAdd)
                    .subscribe((r: any) => {
                        second.longitude = r[0].lon;
                        second.latitude = r[0].lat;

                        let data = {
                            "locations": [
                                {
                                    "departure": first,
                                    "destination": second
                                }
                            ],
                            "vehicleType": form.carType,
                            "babyTransport": true,
                            "petTransport": true
                        }
                        this.unregisteredService
                        .getEstimate(data)
                        .subscribe((res: any) => {
                            let estimation: EstimationDTO = {
                                estimatedCost : res.estimatedCost,
                                estimatedTimeInMinutes : res.estimatedTimeInMinutes,
                                distance: Number((res.distance / 1000).toFixed(2))
                            };
                            this.estimation.emit(estimation);
                            this.isToggled.emit(true);
                            this.waypoints.emit({first, second});
                            if (form.carType) this.vehicleType.emit({viewValue: "", value: form.carType});
                        });
                    });
                });
            }

        }
    }
}

