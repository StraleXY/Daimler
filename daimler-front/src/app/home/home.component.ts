import { Component } from '@angular/core';
import { EstimationDTO } from '../common/get-a-ride/get-a-ride.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

  isToggled = false;
  first = "";
  second = "";

  scroll(el: HTMLElement){
    el.scrollIntoView(true);
  }

  toggle(toggled: boolean) : void{
      this.isToggled = toggled;
  }

  estimation: EstimationDTO = {estimatedTimeInMinutes:0, distance:0, estimatedCost:0};
  waypoints: any = {}

  estimate(est: EstimationDTO) : void{
      this.estimation = est;
      this.first = "";
      this.second = "";
  }

  waypointEvent(e: any) {
      this.waypoints = e;
  }

  setFirst(e: any) {
      this.first = e;
  }

  setSecond(e: any) {
      this.second = e;
  }
}
