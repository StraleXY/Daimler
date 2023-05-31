package com.tim1.daimlerback.dtos.common;

import com.tim1.daimlerback.entities.Location;

public class DepartureDestinationDTO {
    private LocationDTO departure;
    private LocationDTO destination;

    public DepartureDestinationDTO() {

    }

    public DepartureDestinationDTO(Location departure, Location destination) {
        this.departure = new LocationDTO(departure);
        this.destination = new LocationDTO(destination);
    }

    public DepartureDestinationDTO(LocationDTO departure, LocationDTO destination) {
        this.departure = departure;
        this.destination = destination;
    }

    public DepartureDestinationDTO(Location location) {
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
