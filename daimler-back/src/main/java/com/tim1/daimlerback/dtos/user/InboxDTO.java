package com.tim1.daimlerback.dtos.user;

import com.tim1.daimlerback.entities.Driver;
import com.tim1.daimlerback.entities.Message;
import com.tim1.daimlerback.entities.Passenger;
import com.tim1.daimlerback.entities.User;

public class InboxDTO {
    SimpleUserDTO with;
    MessageDTO lastMessage;
    String destination;
    public InboxDTO() {
    }
    public InboxDTO(User user, Message message, String destination) {
        this.with = new SimpleUserDTO(user);
        this.lastMessage = new MessageDTO(message);
        this.destination = destination;
    }
    public InboxDTO(Message message) {
        this.lastMessage = new MessageDTO(message);
    }

    public SimpleUserDTO getWith() {
        return with;
    }
    public void setWith(SimpleUserDTO with) {
        this.with = with;
    }
    public MessageDTO getLastMessage() {
        return lastMessage;
    }
    public void setLastMessage(MessageDTO lastMessage) {
        this.lastMessage = lastMessage;
    }
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
}
