package com.tim1.daimler.model;

import java.io.Serializable;

public class User implements Serializable {

    String name;
    String surname;

    public User(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public String getFullName() {
        return name + " " + surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}
