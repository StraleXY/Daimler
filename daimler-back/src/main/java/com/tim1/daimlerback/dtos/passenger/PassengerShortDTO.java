package com.tim1.daimlerback.dtos.passenger;

import com.tim1.daimlerback.entities.Passenger;

public class PassengerShortDTO {
    private Integer id;
    private String email;
    public PassengerShortDTO() {

    }

    public PassengerShortDTO(Passenger passenger) {
        id = passenger.getId();
        email = passenger.getEmail();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
