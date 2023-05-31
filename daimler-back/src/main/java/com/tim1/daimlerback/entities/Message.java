package com.tim1.daimlerback.entities;

import jakarta.persistence.*;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //TODO Add date
    @Column
    private Integer senderId;
    @Column
    private Integer receiverId;
    @Column
    private String message;
    @Column
    private String type;
    @Column
    private long timestamp;
    @Column
    private Integer rideId;

    public Message() {
    }

    public Message(Integer senderId, Integer receiverId, String message, String type, long timestamp, Integer rideId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
        this.rideId = rideId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getRideId() {
        return rideId;
    }

    public void setRideId(Integer rideId) {
        this.rideId = rideId;
    }

    public Integer messageWith(Integer userId) {
        return senderId.equals(userId) ? receiverId : senderId;
    }
}
