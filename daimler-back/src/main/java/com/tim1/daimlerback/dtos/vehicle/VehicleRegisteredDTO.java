package com.tim1.daimlerback.dtos.vehicle;

import com.tim1.daimlerback.dtos.common.LocationDTO;
import com.tim1.daimlerback.entities.Vehicle;

public class VehicleRegisteredDTO {
    private String model;
    private String licenseNumber;
    private String vehicleType;
    private Integer passengerSeats;
    private Boolean babyTransport;
    private Boolean petTransport;
    private LocationDTO currentLocation;
    private Integer driverId;
    private Integer id;

    public VehicleRegisteredDTO(Vehicle vehicle) {
        this.model = vehicle.getModel();
        this.licenseNumber = vehicle.getLicenseNumber();
        this.vehicleType= vehicle.getVehicleType();
        this.passengerSeats = vehicle.getPassengerSeats();
        this.babyTransport = vehicle.getBabyTransport();
        this.petTransport = vehicle.getPetTransport();
        this.currentLocation = new LocationDTO(vehicle.getCurrentLocation());
        this.driverId = vehicle.getDriver().getId();
        this.id = vehicle.getId();
    }

    public VehicleRegisteredDTO() {

    }
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Integer getPassengerSeats() {
        return passengerSeats;
    }

    public void setPassengerSeats(Integer passengerSeats) {
        this.passengerSeats = passengerSeats;
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

    public LocationDTO getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationDTO currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
