package com.tim1.daimler.model;

import com.tim1.daimler.dtos.user.UserInRideDTO;

import java.io.Serializable;

public class Message implements Serializable {

    String message;
    UserInRideDTO sender;
    long createdAt;
    Types type;

    public Message(String message, UserInRideDTO sender, long createdAt, Types type) {
        this.message = message;
        this.sender = sender;
        this.createdAt = createdAt;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public UserInRideDTO getSender() {
        return sender;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public Types getType() {
        return type;
    }


    public enum Types implements Serializable {
        SENT(0), RECEIVED(1), NOTIFICATION(2);

        private int val;

        private Types(int value){
            val = value;
        }

        public int getValue(){
            return val;
        }
    }
}
