import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { InvitationDTO, InvitationResponseDTO, RideDTO } from '../entities/rides';

@Injectable({
  providedIn: 'root'
})
export class RideService {

  constructor(private http: HttpClient) { }

  getRide(id : number): Observable<RideDTO> {
    return this.http.get<RideDTO>(environment.apiHost + "ride/" + id);
  }

  invite(dto : InvitationDTO): Observable<any> {
    return this.http.post<any>(environment.apiHost + "passenger/invite", dto);
  }

  invitationResponse(dto : InvitationResponseDTO): Observable<any> {
    return this.http.post<any>(environment.apiHost + "passenger/invitationResponse", dto);
  }

  start(id : number): Observable<any> {
    return this.http.put<any>(environment.apiHost + "ride/" + id + "/start", null);
  }

  end(id : number): Observable<any> {
    return this.http.put<any>(environment.apiHost + "ride/" + id + "/end", null);
  }

  reject(id : number, reason: string): Observable<any> {
    return this.http.put<any>(environment.apiHost + "ride/" + id + "/cancel", {reason: reason});
  }

  panic(id : number, reason: string): Observable<any> {
    return this.http.put<any>(environment.apiHost + "ride/" + id + "/panic", {reason: reason});
  }
}
