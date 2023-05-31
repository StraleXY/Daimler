package com.tim1.daimlerback.dtos.panic;

import com.tim1.daimlerback.dtos.common.RegisterDTO;
import com.tim1.daimlerback.dtos.passenger.PassengerDTO;
import com.tim1.daimlerback.dtos.ride.CreatedRideDTO;

public class PanicRideDTO {
    private Integer id;
    private RegisterDTO user;
    private CreatedRideDTO ride;
    private String time;
    private String reason;
    private Integer vehicleId;

    public PanicRideDTO() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RegisterDTO getUser() {
        return user;
    }

    public void setUser(RegisterDTO user) {
        this.user = user;
    }

    public CreatedRideDTO getRide() {
        return ride;
    }

    public void setRide(CreatedRideDTO ride) {
        this.ride = ride;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }
}
