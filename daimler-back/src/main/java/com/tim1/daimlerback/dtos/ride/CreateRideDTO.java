package com.tim1.daimlerback.dtos.ride;

import com.tim1.daimlerback.dtos.common.DepartureDestinationDTO;
import com.tim1.daimlerback.dtos.passenger.PassengerShortDTO;

import java.util.List;

public class CreateRideDTO {
    private List<DepartureDestinationDTO> locations;
    private List<PassengerShortDTO> passengers;
    private String vehicleType;
    private Boolean babyTransport;
    private Boolean petTransport;
    private Long scheduledTimestamp;

    public CreateRideDTO() {

    }

    public List<DepartureDestinationDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<DepartureDestinationDTO> locations) {
        this.locations = locations;
    }

    public List<PassengerShortDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerShortDTO> passengers) {
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

    public Long getScheduledTimestamp() {
        return scheduledTimestamp;
    }

    public void setScheduledTimestamp(Long scheduledTimestamp) {
        this.scheduledTimestamp = scheduledTimestamp;
    }
}