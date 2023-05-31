package com.tim1.daimlerback.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("vehicle")
public class VehicleReview extends Review {

    @ManyToOne(cascade = CascadeType.REFRESH)
    Vehicle vehicle;

    public VehicleReview() {
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
