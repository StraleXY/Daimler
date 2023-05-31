package com.tim1.daimler.dtos.ride;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AssumptionDTO implements Serializable {
    private List<DepartureDestinationDTO> locations;
    private String vehicleType;
    private Boolean babyTransport;
    private Boolean petTransport;

    public AssumptionDTO() {

    }

    public List<DepartureDestinationDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<DepartureDestinationDTO> locations) {
        this.locations = locations;
    }

    public void setLocation(DepartureDestinationDTO location) {
        this.locations = new ArrayList<>();
        this.locations.add(location);
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
}
