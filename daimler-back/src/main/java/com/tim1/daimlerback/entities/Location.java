package com.tim1.daimlerback.entities;

import com.tim1.daimlerback.dtos.common.DepartureDestinationDTO;
import com.tim1.daimlerback.dtos.common.LocationDTO;
import jakarta.persistence.*;

@Entity
public class Location {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String address;
    @Column private double longitude;
    @Column private double latitude;

    public Location() {
    }

    public Location(LocationDTO dto) {
        this.latitude = dto.getLatitude();
        this.longitude = dto.getLongitude();
        this.address = dto.getAddress();
    }


    public Location(String address, double longitude, double latitude) {
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
