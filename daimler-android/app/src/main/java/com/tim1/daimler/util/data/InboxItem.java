package com.tim1.daimler.util.data;

import com.tim1.daimler.dtos.message.MessageDTO;
import com.tim1.daimler.dtos.message.MessagesDTO;
import com.tim1.daimler.model.Message;

import java.io.Serializable;

public interface InboxItem {

    String getPersonName();
    String getDestination();
    MessageDTO getLastMessage();
    Boolean getIsPinned();
}
