package com.tim1.daimlerback.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Note {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String date;

    @Column
    private String message;

    @Column
    private Integer userId;

    public Note() {
    }

    public Note(String date, String message, Integer userId) {
        this.date = date;
        this.message = message;
        this.userId = userId;
    }

    public Note(String message, Integer userId) {
        this.date = LocalDateTime.now().toString();
        this.message = message;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
