package com.tim1.daimler.dtos.message;

import com.tim1.daimler.util.data.InboxItem;

import java.io.Serializable;

public class InboxDTO  implements Serializable, InboxItem {
    SimpleUserDTO with;
    MessageDTO lastMessage;
    String destination;

    public InboxDTO() {}

    public SimpleUserDTO getWith() {
        return with;
    }
    public void setWith(SimpleUserDTO with) {
        this.with = with;
    }
    public void setLastMessage(MessageDTO lastMessage) {
        this.lastMessage = lastMessage;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public MessageDTO getLastMessage() {
        return lastMessage;
    }
    @Override
    public Boolean getIsPinned() {
        return lastMessage.getType().equals("SUPPORT");
    }
    @Override
    public String getDestination() {
        return destination;
    }
    @Override
    public String getPersonName() {
        return with.getName() + " " + with.getSurname();
    }
}
