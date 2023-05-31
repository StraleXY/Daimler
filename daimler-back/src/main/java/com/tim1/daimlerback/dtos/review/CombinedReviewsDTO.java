package com.tim1.daimlerback.dtos.review;

import com.tim1.daimlerback.entities.DriverReview;
import com.tim1.daimlerback.entities.VehicleReview;

public class CombinedReviewsDTO {
    private ReviewDTO vehicleReview;
    private ReviewDTO driverReview;

    public CombinedReviewsDTO() {

    }

    public CombinedReviewsDTO(DriverReview driverReview, VehicleReview vehicleReview) {
        this.vehicleReview = vehicleReview == null ? null : new ReviewDTO(vehicleReview);
        this.driverReview = driverReview == null ? null : new ReviewDTO(driverReview);
    }

    public ReviewDTO getVehicleReview() {
        return vehicleReview;
    }

    public void setVehicleReview(ReviewDTO vehicleReview) {
        this.vehicleReview = vehicleReview;
    }

    public ReviewDTO getDriverReview() {
        return driverReview;
    }

    public void setDriverReview(ReviewDTO driverReview) {
        this.driverReview = driverReview;
    }
}
