package com.tim1.daimlerback.entities;


import com.tim1.daimlerback.dtos.common.DepartureDestinationDTO;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class Ride {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String startTime;
    @Column private String endTime;
    @Column private Integer totalCost;
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER) private Driver driver;
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany (cascade = {CascadeType.REFRESH}) private Collection<Passenger> passengers = new ArrayList<>();
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany (cascade = {CascadeType.ALL}) private Collection<Location> locations = new ArrayList<>();
    @Column private Integer estimatedTimeInMinutes;

    //TODO Lista recenzija
    @Column private String status;
    @OneToOne (cascade = {CascadeType.ALL}, fetch = FetchType.EAGER) private Rejection rejection;
    @Column private Boolean isPanic;
    @Column private Boolean babyTransport;
    @Column private Boolean petTransport;
    @Column private String vehicleType;
    @Column private Double distance;
    @Column private Long scheduledTimestamp;

    public Ride() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Collection<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(Collection<Passenger> passengers) {
        this.passengers = passengers;
    }

    public Collection<Location> getLocations() {
        return locations;
    }

    public void setLocations(Collection<Location> locations) {
        this.locations = locations;
    }

    public Integer getEstimatedTimeInMinutes() {
        return estimatedTimeInMinutes;
    }

    public void setEstimatedTimeInMinutes(Integer estimatedTimeInMinutes) {
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Rejection getRejection() {
        return rejection;
    }

    public void setRejection(Rejection rejection) {
        this.rejection = rejection;
    }

    public Boolean getPanic() {
        return isPanic;
    }

    public void setPanic(Boolean panic) {
        isPanic = panic;
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

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setLocations(List<DepartureDestinationDTO> locations) {
        this.locations = new ArrayList<>();
        for(int i = 0; i < locations.size(); i++) {
            this.locations.add(new Location(locations.get(i).getDeparture()));
            if(i == locations.size() - 1) this.locations.add(new Location(locations.get(i).getDestination()));
        }
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Long getScheduledTimestamp() {
        return scheduledTimestamp;
    }

    public void setScheduledTimestamp(Long scheduledTime) {
        this.scheduledTimestamp = scheduledTime;
    }

    public String getDestination() {
        return locations.stream().toList().get(locations.size() - 1).getAddress();
    }
}
