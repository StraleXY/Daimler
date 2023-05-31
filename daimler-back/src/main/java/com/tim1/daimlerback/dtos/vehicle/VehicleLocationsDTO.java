package com.tim1.daimlerback.dtos.vehicle;

import java.util.List;

public class VehicleLocationsDTO {
    private List<LatLongDTO> locations;

    public VehicleLocationsDTO() {

    }

    public List<LatLongDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<LatLongDTO> locations) {
        this.locations = locations;
    }
}
