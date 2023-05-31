package com.tim1.daimlerback.dtos.passenger;

import java.util.List;

public class PassengersDTO {
    private Integer totalCount;
    private List<PassengerDTO> results;

    public PassengersDTO() {

    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public List<PassengerDTO> getResults() {
        return results;
    }

    public void setResults(List<PassengerDTO> results) {
        this.results = results;
    }
}
