package com.tim1.daimler.dtos.message;

import java.util.ArrayList;
import java.util.List;

public class MessagesDTO {
    private Integer totalCount;
    private List<MessageDTO> results;

    public MessagesDTO() {
        totalCount = 0;
        results = new ArrayList<>();
    }

    public List<MessageDTO> getNew(MessagesDTO arrivedMessages) {
        List<MessageDTO> newMessages = new ArrayList<>();
        for(MessageDTO message : arrivedMessages.getResults()) {
            if(!this.results.contains(message)) {
                this.results.add(message);
                newMessages.add(message);
            }
        }
        this.totalCount = arrivedMessages.totalCount;
        return newMessages;
    }

    public void addNew(MessageDTO message) {
        results.add(message);
        totalCount++;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public List<MessageDTO> getResults() {
        return results;
    }

    public void setResults(List<MessageDTO> results) {
        this.results = results;
    }
}
