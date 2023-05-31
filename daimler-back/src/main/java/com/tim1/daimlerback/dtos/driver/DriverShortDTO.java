package com.tim1.daimlerback.dtos.driver;

import com.tim1.daimlerback.entities.Driver;

public class DriverShortDTO {
    private Integer id;
    private String email;
    public DriverShortDTO() {

    }

    public DriverShortDTO(Driver driver) {
        id = driver.getId();
        email = driver.getEmail();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
