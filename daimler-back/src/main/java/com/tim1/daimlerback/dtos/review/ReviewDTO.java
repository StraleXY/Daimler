package com.tim1.daimlerback.dtos.review;

import com.tim1.daimlerback.dtos.passenger.PassengerShortDTO;
import com.tim1.daimlerback.entities.DriverReview;
import com.tim1.daimlerback.entities.Review;
import com.tim1.daimlerback.entities.VehicleReview;

public class ReviewDTO {
    private Integer id;
    private Integer rating;
    private String comment;
    private PassengerShortDTO passenger;

    public ReviewDTO() {}

    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.passenger = new PassengerShortDTO(review.getPassenger());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PassengerShortDTO getPassenger() {
        return passenger;
    }

    public void setPassenger(PassengerShortDTO passenger) {
        this.passenger = passenger;
    }
}
