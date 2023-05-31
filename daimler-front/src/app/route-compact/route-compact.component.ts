import { trigger, state, style, transition, animate } from '@angular/animations';
import { Component, Input } from '@angular/core';
import { EstimationDTO } from '../common/get-a-ride/get-a-ride.component';

@Component({
  selector: 'app-route-compact',
  templateUrl: './route-compact.component.html',
  styleUrls: ['./route-compact.component.css']
})
export class RouteCompactComponent {

    @Input() data: EstimationDTO = {estimatedCost:0, estimatedTimeInMinutes:0, distance:0};

}
