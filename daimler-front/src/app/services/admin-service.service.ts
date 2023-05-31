import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Admin, UpdateAdminDTO } from '../entities/admin';
import { UpdateUserDTO } from '../entities/user';

@Injectable({
  providedIn: 'root'
})
export class AdminServiceService {

  constructor(private http: HttpClient) { }

  getAdmin(id: Number): Observable<Admin> {
    return this.http.get<Admin>(environment.apiHost + "admin/" + id);
  }

  updateAdmin(id: Number, user: UpdateAdminDTO): Observable<any> {
    return this.http.put<UpdateAdminDTO>(environment.apiHost + 'admin/' + id, user);
  }

  registerDriver(driver: any): Observable<any> {
    return this.http.post<string>(environment.apiHost + 'driver', driver);
  }

  registerVehicle(id: number, vehicle: any): Observable<any> {
    return this.http.post<string>(environment.apiHost + 'driver/' + id + "/vehicle", vehicle);
  }

  updateDriverPhoto(id: Number, user: any): Observable<any> {
    return this.http.put<string>(environment.apiHost + 'driver/' + id, user);
  }

  approveDriverRequest(id : Number, user : UpdateUserDTO) : Observable<any> {
    return this.http.put<UpdateUserDTO>(environment.apiHost + 'admin/request/' + id, user);
  }
}
