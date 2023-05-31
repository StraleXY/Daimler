package com.tim1.daimlerback.dtos.common;

import com.tim1.daimlerback.entities.Driver;
import com.tim1.daimlerback.entities.Passenger;
import com.tim1.daimlerback.entities.User;

public class RegisterDTO {
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;
    private String password;

    public RegisterDTO() {

    }

    public RegisterDTO(Driver u) {
        this.name = u.getName();
        this.surname = u.getSurname();
        this.profilePicture = u.getProfilePicture();
        this.email = u.getEmail();
        this.address = u.getAddress();
        this.telephoneNumber = u.getTelephoneNumber();
        this.password = "";
    }

    public RegisterDTO(Passenger u) {
        this.name = u.getName();
        this.surname = u.getSurname();
        this.profilePicture = u.getProfilePicture();
        this.email = u.getEmail();
        this.address = u.getAddress();
        this.telephoneNumber = u.getTelephoneNumber();
        this.password = "";
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

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
