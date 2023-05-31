import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CombinedReviewsDTO, RateDTO, ReviewDTO } from '../entities/reviews';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  constructor(private http: HttpClient) { }

  getRideReviews(rideId : number) : Observable<CombinedReviewsDTO[]> {
    return this.http.get<CombinedReviewsDTO[]>(environment.apiHost + 'review/' + rideId);
  }

  postVehicleRating(rideId : number, rate : RateDTO) : Observable<ReviewDTO> {
    return this.http.post<ReviewDTO>(environment.apiHost + 'review/' + rideId + '/vehicle', rate);
  }

  postDriverRating(rideId : number, rate : RateDTO) : Observable<ReviewDTO> {
    return this.http.post<ReviewDTO>(environment.apiHost + 'review/' + rideId + '/driver', rate);
  }
}
