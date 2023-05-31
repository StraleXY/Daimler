package com.tim1.daimlerback.dtos.user;

import java.util.List;

public class MessagesDTO {
    private Integer totalCount;
    private List<MessageDTO> results;

    public MessagesDTO() {

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
