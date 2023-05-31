package com.tim1.daimlerback.dtos.user;

import com.tim1.daimlerback.entities.Driver;
import com.tim1.daimlerback.entities.Passenger;
import com.tim1.daimlerback.entities.enumeration.ERole;

public class UserDTO {

    private Integer id;
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;
    private boolean isBlocked;
    private ERole role;

    public UserDTO(Passenger passenger) {
        this.id = passenger.getId();
        this.name = passenger.getName();
        this.surname = passenger.getSurname();
        this.profilePicture = passenger.getProfilePicture();
        this.telephoneNumber = passenger.getTelephoneNumber();
        this.email = passenger.getEmail();
        this.address = passenger.getAddress();
        this.isBlocked = passenger.getBlocked();
        this.role = ERole.ROLE_PASSENGER;
    }

    public UserDTO(Driver driver) {
        this.id = driver.getId();
        this.name = driver.getName();
        this.surname = driver.getSurname();
        this.profilePicture = driver.getProfilePicture();
        this.telephoneNumber = driver.getTelephoneNumber();
        this.email = driver.getEmail();
        this.address = driver.getAddress();
        this.isBlocked = driver.getBlocked();
        this.role = ERole.ROLE_DRIVER;
    }

    public UserDTO() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public ERole getRole() {
        return role;
    }

    public void setRole(ERole role) {
        this.role = role;
    }
}
