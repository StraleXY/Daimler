package com.tim1.daimlerback.dtos.user;

import com.tim1.daimlerback.entities.Driver;
import com.tim1.daimlerback.entities.Passenger;

public class UserInRideDTO {
    private Integer id;
    private String email;
    private String name;
    private String surname;
    private String profilePicture;

    public UserInRideDTO() {}

    public UserInRideDTO(Passenger passenger) {
        this.id = passenger.getId();
        this.email = passenger.getEmail();
        this.name = passenger.getName();
        this.surname = passenger.getSurname();
        this.profilePicture = passenger.getProfilePicture();
    }

    public UserInRideDTO(Driver driver) {
        this.id = driver.getId();
        this.email = driver.getEmail();
        this.name = driver.getName();
        this.surname = driver.getSurname();
        this.profilePicture = driver.getProfilePicture();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
