package com.tim1.daimlerback.entities;

import com.tim1.daimlerback.entities.enumeration.ERole;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("driver")
public class Driver extends User {

    @Lob
    @Column(name = "profile_photo", columnDefinition = "BLOB")
    private byte[] photo;

    @Column
    private String telephoneNumber;

    @Column
    private String address;

    @Column
    private Boolean isBlocked;

    @Column
    private Boolean isActive;
    @Column
    private Boolean isBusy;

    @OneToOne(targetEntity = Vehicle.class)
    private Vehicle vehicle;

    public Driver() {
        setRole(ERole.ROLE_DRIVER);
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Boolean getBusy() {
        return isBusy;
    }

    public void setBusy(Boolean busy) {
        isBusy = busy;
    }
}
