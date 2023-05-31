package com.tim1.daimlerback.dtos.admin;

import com.tim1.daimlerback.entities.Admin;

public class AdminDTO {

    private Integer id;
    private String name;
    private String surname;
    private String email;

    public AdminDTO() {

    }

    public AdminDTO(Admin admin) {
        this.id = admin.getId();
        this.name = admin.getName();
        this.surname = admin.getSurname();
        this.email = admin.getEmail();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
