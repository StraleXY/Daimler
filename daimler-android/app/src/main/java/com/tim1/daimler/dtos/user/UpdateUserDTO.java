package com.tim1.daimler.dtos.user;

public class UpdateUserDTO {
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;
    private String password;

    public UpdateUserDTO() {
        this.name = "";
        this.surname = "";
        this.profilePicture = "";
        this.telephoneNumber = "";
        this.email = "";
        this.address = "";
        this.password = "";
    }

    public UpdateUserDTO(UpdateUserDTO data){
        this.name = data.getName();
        this.surname = data.getSurname();
        this.profilePicture = data.getProfilePicture();
        this.telephoneNumber = data.getTelephoneNumber();
        this.email = data.getEmail();
        this.address = data.getAddress();
        this.password = data.getPassword();
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
