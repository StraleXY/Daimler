package com.tim1.daimlerback.entities;

import jakarta.persistence.*;

@Entity
public class Rejection {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String reason;
    @Column private String timestamp;

    public Rejection() {
    }

    public Rejection(String reason, String timestamp) {
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
