package com.tim1.daimler.dtos.user;

import com.tim1.daimler.dtos.message.SimpleUserDTO;

import java.io.Serializable;

public class UserInRideDTO implements Serializable {
    private Integer id;
    private String email;
    private String name;
    private String surname;
    private String profilePicture;

    public UserInRideDTO() {}

    public UserInRideDTO(UserDTO userDTO) {
        this.id = userDTO.getId();
        this.email = userDTO.getEmail();
        this.name = userDTO.getName();
        this.surname = userDTO.getSurname();
        this.profilePicture = userDTO.getProfilePicture();
    }

    public UserInRideDTO(SimpleUserDTO userDTO) {
        this.id = userDTO.getId();
        this.email = userDTO.getEmail();
        this.name = userDTO.getName();
        this.surname = userDTO.getSurname();
        this.profilePicture = "";
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
