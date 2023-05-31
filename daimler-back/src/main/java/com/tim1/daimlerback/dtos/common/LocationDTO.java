package com.tim1.daimlerback.dtos.common;

import com.tim1.daimlerback.entities.Location;

public class LocationDTO {
    private Integer id;
    private String address;
    private Double latitude;
    private Double longitude;

    public LocationDTO() {

    }

    public LocationDTO(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = "";
    }

    public LocationDTO(Location location) {
        id = location.getId();
        address = location.getAddress();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
