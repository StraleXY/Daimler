package com.tim1.daimler.dtos.ride;

import java.io.Serializable;

public class DepartureDestinationDTO implements Serializable {
    private LocationDTO departure;
    private LocationDTO destination;

    public DepartureDestinationDTO() {

    }

    public DepartureDestinationDTO(LocationDTO departure, LocationDTO destination) {
        this.departure = departure;
        this.destination = destination;
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
