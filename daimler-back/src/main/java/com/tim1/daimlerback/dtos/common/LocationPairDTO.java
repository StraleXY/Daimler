package com.tim1.daimlerback.dtos.common;

public class LocationPairDTO {
    private LocationDTO departure;
    private LocationDTO destination;
    public LocationPairDTO() {

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
}
