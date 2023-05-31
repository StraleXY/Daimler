import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})

export class UnregisteredService {

    constructor(private http: HttpClient) { }

    getEstimate(data: any): Observable<any> {
        return this.http.post<string>(environment.apiHost + "unregisteredUser/", data);
    }

    geoDecode(number: string, address: string): Observable<any> {
        return this.http.get<string>(
            "https://nominatim.openstreetmap.org/search?q="+
                number+"+"+address+",+Novi+Sad&format=json&polygon=1&addressdetails=1");
    }

    reverseDecode(latlng: any): Observable<any> {
        return this.http.get<string>("https://nominatim.openstreetmap.org/reverse?lat=" + latlng.lat + "&lon=" + latlng.lng+ "&format=json");
    }

}
