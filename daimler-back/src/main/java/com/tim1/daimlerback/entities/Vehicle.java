package com.tim1.daimlerback.entities;

import com.tim1.daimlerback.dtos.common.LocationDTO;
import jakarta.persistence.*;

@Entity
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(mappedBy = "vehicle")
    private Driver driver;

    @Column
    private String model;

    @Column
    private String licenseNumber;

    //TODO: VehicleType should be separate entity
    @Column
    private String vehicleType;

    @Column
    private Integer passengerSeats;

    @Column
    private Boolean babyTransport;

    @Column
    private Boolean petTransport;

    @OneToOne Location location;
    public Vehicle() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Location getCurrentLocation() {
        return location;
    }

    public void setCurrentLocation(Location location) {
        this.location = location;
    }
}
