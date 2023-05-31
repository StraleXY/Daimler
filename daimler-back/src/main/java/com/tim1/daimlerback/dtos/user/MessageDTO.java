package com.tim1.daimlerback.dtos.user;

import com.tim1.daimlerback.entities.Message;

public class MessageDTO {
    private Integer id;
    private String timeOfSending;
    private Integer senderId;
    private Integer receiverId;
    private String message;
    private String type;
    private Integer rideId;
    private long timestamp;

    public MessageDTO() {

    }

    public MessageDTO(Message message) {
        this.id = message.getId();
        // TODO Laza
        this.timeOfSending = "Laza ce ovo :)";
        this.senderId = message.getSenderId();
        this.receiverId = message.getReceiverId();
        this.message = message.getMessage();
        this.type = message.getType();
        this.rideId = message.getRideId();
        this.timestamp = message.getTimestamp();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTimeOfSending() {
        return timeOfSending;
    }

    public void setTimeOfSending(String timeOfSending) {
        this.timeOfSending = timeOfSending;
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

    public Integer getRideId() {
        return rideId;
    }

    public void setRideId(Integer rideId) {
        this.rideId = rideId;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
