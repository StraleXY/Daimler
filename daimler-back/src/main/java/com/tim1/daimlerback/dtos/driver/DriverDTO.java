package com.tim1.daimlerback.dtos.driver;

import com.tim1.daimlerback.dtos.passenger.UpdateUserDTO;
import com.tim1.daimlerback.entities.Driver;
import com.tim1.daimlerback.entities.DriverUpdateRequest;

public class DriverDTO {
    private Integer id;
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;

    public DriverDTO(Driver driver) {
        this.id = driver.getId();
        this.name = driver.getName();
        this.surname = driver.getSurname();
        this.profilePicture = driver.getProfilePicture();
        this.telephoneNumber = driver.getTelephoneNumber();
        this.email = driver.getEmail();
        this.address = driver.getAddress();
    }

    public DriverDTO() {

    }

    public DriverDTO(DriverUpdateRequest request) {
        this.id = request.getDriverId();
        this.name = request.getName();
        this.surname = request.getSurname();
        this.profilePicture = request.getProfilePicture();
        this.telephoneNumber = request.getTelephoneNumber();
        this.email = request.getEmail();
        this.address = request.getAddress();
    }

    public DriverDTO(Integer id, UpdateUserDTO driverDTO) {
        this.id = id;
        this.name = driverDTO.getName();
        this.surname = driverDTO.getSurname();
        this.profilePicture = driverDTO.getProfilePicture();
        this.telephoneNumber = driverDTO.getTelephoneNumber();
        this.email = driverDTO.getEmail();
        this.address = driverDTO.getAddress();
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
}
