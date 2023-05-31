import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { UserRidesDTO, RideDTO, DepartureDestinationDTO, FavoriteRouteDTO } from '../entities/rides';
import { UserFull, UpdateUserDTO } from '../entities/user';

@Injectable({
  providedIn: 'root'
})
export class PassengerService {

  constructor(private http: HttpClient) { }

  get(id : number): Observable<UserFull> {
    return this.http.get<UserFull>(environment.apiHost + "passenger/" + id);
  }

  updatePassenger(id : Number, user : UpdateUserDTO) : Observable<any> {
    return this.http.put<UpdateUserDTO>(environment.apiHost + 'passenger/' + id, user);
  }

  getRides(id: number, page: number, size: number, sort: string, from: string, to: string) : Observable<UserRidesDTO>  {
    return this.http.get<UserRidesDTO>(environment.apiHost + "passenger/" + id + "/ride?page=" + page + "&size=" + size + "&sort=" + sort + "&from=" + from + "&to=" + to);
  }

  postFavoriteRoute(passengerId: number, departureId: number, destinationId: number) : Observable<FavoriteRouteDTO> {
    return this.http.post<FavoriteRouteDTO>(environment.apiHost + "ride/" + passengerId + "/favorite?departureId=" + departureId + "&destinationId=" + destinationId, {});
  }

  getFavoriteRoutes(passengerId: number) : Observable<FavoriteRouteDTO[]> {
    return this.http.get<FavoriteRouteDTO[]>(environment.apiHost + "ride/" + passengerId + "/favorite");
  }

  createRide(data: any) : Observable<RideDTO> {
    return this.http.post<RideDTO>(environment.apiHost + "ride", data);
  }

  getActiveRide(id: string) : Observable<any> {
    return this.http.get<any>(environment.apiHost + "ride/passenger/" + id + "/active");
  }

}
