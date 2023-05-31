package com.tim1.daimler.dtos.review;

import com.tim1.daimler.dtos.passenger.PassengerShortDTO;

public class ReviewDTO {
    private Integer id;
    private Integer rating;
    private String comment;
    private PassengerShortDTO passenger;

    public ReviewDTO() {}

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
