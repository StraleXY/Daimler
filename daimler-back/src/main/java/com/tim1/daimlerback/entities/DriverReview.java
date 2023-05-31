package com.tim1.daimlerback.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("driver")
public class DriverReview extends Review {

    @ManyToOne(cascade = CascadeType.REFRESH)
    private Driver driver;

    public DriverReview() {
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
