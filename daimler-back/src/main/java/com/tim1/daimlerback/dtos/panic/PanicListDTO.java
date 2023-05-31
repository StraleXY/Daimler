package com.tim1.daimlerback.dtos.panic;

import java.util.List;

public class PanicListDTO {
    private Integer totalCount;
    private List<PanicRideDTO> results;
    public PanicListDTO() {

    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public List<PanicRideDTO> getResults() {
        return results;
    }

    public void setResults(List<PanicRideDTO> results) {
        this.results = results;
    }
}
