package com.tim1.daimlerback.entities;

import com.tim1.daimlerback.dtos.passenger.UpdateUserDTO;
import jakarta.persistence.*;

@Entity
public class DriverUpdateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer driverId;

    private String name;
    private String surname;
    @Lob
    @Column(name = "profile_photo", columnDefinition = "BLOB")
    private byte[] photo;
    private String telephoneNumber;
    private String email;
    private String address;
    private String password;

    public DriverUpdateRequest(Integer driverId, UpdateUserDTO userDTO) {
        this.driverId = driverId;
        this.name = userDTO.getName();
        this.surname = userDTO.getSurname();
        setProfilePicture(userDTO.getProfilePicture());
        this.telephoneNumber = userDTO.getTelephoneNumber();
        this.email = userDTO.getEmail();
        this.address = userDTO.getAddress();
        this.password = userDTO.getPassword();
    }

    public DriverUpdateRequest() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
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
        if (photo != null)
            return new String(photo);
        return "";
    }

    public void setProfilePicture(String profilePicture) {
        this.photo = profilePicture.getBytes();
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
