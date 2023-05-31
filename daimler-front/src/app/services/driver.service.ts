import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { UserRidesDTO } from '../entities/rides';
import { UserFull, UpdateUserDTO } from '../entities/user';
import { VehicleDTO } from '../entities/vehicle';
import { DatePipe } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class DriverService {

  constructor(private http: HttpClient, private datePipe: DatePipe) { }

  get(id : number): Observable<UserFull> {
    return this.http.get<UserFull>(environment.apiHost + "driver/" + id);
  }

  requestUpdate(id : Number, user : UpdateUserDTO) : Observable<any> {
    const options: any = {
      responseType: 'text',
    };
    return this.http.put<void>(environment.apiHost + 'driver/' + id, user, options);
  }

  getRequests() : Observable<UserFull[]> {
    return this.http.get<UserFull[]>(environment.apiHost + 'driver/requests');
  }

  deleteRequest(driverId : Number) : Observable<any> {
    return this.http.delete(environment.apiHost + 'driver/request/' + driverId);
  }

  getRides(id: number, page: number, size: number, sort: string, from: string, to: string) : Observable<UserRidesDTO>  {
    return this.http.get<UserRidesDTO>(environment.apiHost + "driver/" + id + "/ride?page=" + page + "&size=" + size + "&sort=" + sort + "&from=" + from + "&to=" + to);
  }

  getVehicle(id: number) : Observable<VehicleDTO> {
    return this.http.get<VehicleDTO>(environment.apiHost + "driver/" + id + "/vehicle");
  }

  getCurrentDateTime() : string {
    let dateTime = Date.now();
    let dateTimeStr = this.datePipe.transform(dateTime, 'yyyy-MM-dd') + 'T' + this.datePipe.transform(dateTime, 'HH:mm:ss.SSS') + 'Z';
    return dateTimeStr;
  }

  startWorkingHour(driverId: string) : Observable<any> {
    let dateTime = this.getCurrentDateTime();
    return this.http.post<any>(environment.apiHost + "driver/" + driverId + "/working-hour", {'start': dateTime});
  }

  endWorkingHour(id: string) : Observable<any> {
    let dateTime = this.getCurrentDateTime();
    return this.http.put<any>(environment.apiHost + "driver/working-hour/" + id, {"end": dateTime});
  }

  getActiveRide(id: string) : Observable<any> {
    return this.http.get<any>(environment.apiHost + "ride/driver/" + id + "/active");
  }
}
