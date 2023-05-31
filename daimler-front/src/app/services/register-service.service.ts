import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { UserFull } from '../entities/user';
import { User } from '../register/register.component';

@Injectable({
  providedIn: 'root'
})

export class RegisterService {

    constructor(private http: HttpClient) { }

    registerUser(user: User): Observable<UserFull> {
        return this.http.post<UserFull>(environment.apiHost + 'passenger', user);
    }

    validateUser(token: string): Observable<any> {
        const options: any = {
            responseType: 'text',
        };
        return this.http.get<string>(environment.apiHost + 'passenger/activate/' + token, options);
    }
}
