package com.tim1.daimlerback.entities;

import jakarta.persistence.*;

@Entity
public class FavoriteRoute {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER) private Passenger passenger;
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER) private Location departure;
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER) private Location destination;

    public FavoriteRoute() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Location getDeparture() {
        return departure;
    }

    public void setDeparture(Location departure) {
        this.departure = departure;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }
}
