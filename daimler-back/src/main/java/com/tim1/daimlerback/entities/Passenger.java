package com.tim1.daimlerback.entities;

import com.tim1.daimlerback.entities.enumeration.ERole;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;

@Entity
@DiscriminatorValue("passenger")
public class Passenger extends User {

//    @Column
//    private String profilePicture;

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
    private Boolean isEnabled;
    @Column
    private Boolean isBusy;

    public Passenger() {
        setRole(ERole.ROLE_PASSENGER);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
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

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public Boolean getBusy() {
        return isBusy;
    }

    public void setBusy(Boolean busy) {
        isBusy = busy;
    }
}
