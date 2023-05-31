package com.tim1.daimlerback.entities;

import jakarta.persistence.*;
import org.springframework.data.util.Pair;

import java.util.List;

@Entity
public class VehicleMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer rideId;
    @OneToMany(fetch = FetchType.EAGER) private List<Location> locationList;
    @Column
    private Integer current;

    public VehicleMovement() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRideId() {
        return rideId;
    }

    public void setRideId(Integer rideId) {
        this.rideId = rideId;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }
}
