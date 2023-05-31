package com.tim1.daimler.dtos.ride;

import java.util.ArrayList;
import java.util.List;

public class RidesDTO {
    private Integer totalCount;
    private List<CreatedRideDTO> results;

    public RidesDTO() {

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
