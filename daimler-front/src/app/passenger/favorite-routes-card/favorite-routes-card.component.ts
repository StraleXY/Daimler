import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FavoriteRouteDTO } from 'src/app/entities/rides';
import { PassengerService } from 'src/app/services/passenger.service';

@Component({
  selector: 'app-favorite-routes-card',
  templateUrl: './favorite-routes-card.component.html',
  styleUrls: ['./favorite-routes-card.component.css']
})
export class FavoriteRoutesCardComponent {

  constructor(private passengerService : PassengerService, private router: Router) {
    this.passengerService.getFavoriteRoutes(Number.parseInt(localStorage.getItem('userId') ?? '')).subscribe((routes) => {
      this.routes = routes;
      if (this.routes.length == 0) this.noData = true;
      this.lastItemId = this.routes[this.routes.length - 1].id
    });
  }

  noData : boolean = false;
  lastItemId : number = -1;

  routes : FavoriteRouteDTO[] = []

  orderClick(route : FavoriteRouteDTO) {
    this.router.navigate(['passenger/home'], {state: {data: {"departure": route.departure.address, "destination": route.destination.address}}});
  }
}
