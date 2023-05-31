package com.tim1.daimlerback.dtos.driver;

import java.util.List;

public class DriversDTO {
    private Integer totalCount;
    private List<DriverDTO> results;

    public DriversDTO() {

    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public List<DriverDTO> getResults() {
        return results;
    }

    public void setResults(List<DriverDTO> results) {
        this.results = results;
    }
}
