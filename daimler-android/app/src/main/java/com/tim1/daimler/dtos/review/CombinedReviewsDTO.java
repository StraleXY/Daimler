package com.tim1.daimler.dtos.review;

public class CombinedReviewsDTO {
    private ReviewDTO vehicleReview;
    private ReviewDTO driverReview;

    public CombinedReviewsDTO() {

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
