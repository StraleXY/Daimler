import { UserShortDTO } from "./rides";

export interface ReviewDTO {
    id : number,
    rating : number,
    comment : string,
    passenger : UserShortDTO
}

export interface RateDTO {
    rating : number,
    comment : string,
    passengerId : number
}

export interface CombinedReviewsDTO {
    vehicleReview : ReviewDTO,
    driverReview : ReviewDTO
}