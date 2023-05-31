package com.tim1.daimlerback.dtos.common;

import java.util.List;

public class AssumptionDTO {
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
