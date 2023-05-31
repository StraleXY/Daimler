package com.tim1.daimler.dtos.passenger;

import com.tim1.daimler.dtos.ride.LocationDTO;

public class FavoriteRouteDTO {
    private Integer id;
    private LocationDTO departure;
    private LocationDTO destination;
    private Integer passengerId;

    public FavoriteRouteDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocationDTO getDeparture() {
        return departure;
    }

    public void setDeparture(LocationDTO departure) {
        this.departure = departure;
    }

    public LocationDTO getDestination() {
        return destination;
    }

    public void setDestination(LocationDTO destination) {
        this.destination = destination;
    }

    public Integer getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Integer passengerId) {
        this.passengerId = passengerId;
    }
}
