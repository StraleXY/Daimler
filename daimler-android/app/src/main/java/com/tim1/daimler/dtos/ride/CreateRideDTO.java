package com.tim1.daimler.dtos.ride;

import com.tim1.daimler.dtos.user.UserShortDTO;

import java.util.ArrayList;
import java.util.List;

public class CreateRideDTO {
    private List<DepartureDestinationDTO> locations;
    private List<UserShortDTO> passengers;
    private String vehicleType;
    private Boolean babyTransport;
    private Boolean petTransport;
    private long scheduledTimestamp;

    public CreateRideDTO() {

    }

    public List<DepartureDestinationDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<DepartureDestinationDTO> locations) {
        this.locations = locations;
    }

    public List<UserShortDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<UserShortDTO> passengers) {
        this.passengers = passengers;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Boolean getBabyTransport() {
        return babyTransport;
    }

    public void setBabyTransport(Boolean babyTransport) {
        this.babyTransport = babyTransport;
    }

    public Boolean getPetTransport() {
        return petTransport;
    }

    public void setPetTransport(Boolean petTransport) {
        this.petTransport = petTransport;
    }

    public void setPassenger(UserShortDTO passenger) {
        this.passengers = new ArrayList<>();
        this.passengers.add(passenger);
    }

    public long getScheduledTimestamp() {
        return scheduledTimestamp;
    }

    public void setScheduledTimestamp(long scheduledTimestamp) {
        this.scheduledTimestamp = scheduledTimestamp;
    }
}
