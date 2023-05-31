package com.tim1.daimlerback.dtos.common;

import com.tim1.daimlerback.dtos.ride.CreatedRideDTO;

import java.util.ArrayList;
import java.util.List;

public class RidesDTO {
    private Integer totalCount;
    private List<CreatedRideDTO> results;

    public RidesDTO() {

    }

    public RidesDTO(Integer count) {
        this.totalCount = count;
        this.results = new ArrayList<>();
        results.add(new CreatedRideDTO());
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public List<CreatedRideDTO> getResults() {
        return results;
    }

    public void setResults(List<CreatedRideDTO> results) {
        this.results = results;
    }
}
