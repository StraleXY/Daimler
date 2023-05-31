package com.tim1.daimlerback.dtos.driver;

import java.util.List;

public class WorkingHoursDTO {
    private Integer totalCount;
    private List<WorkingHourDTO> results;

    public WorkingHoursDTO() {

    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public List<WorkingHourDTO> getResults() {
        return results;
    }

    public void setResults(List<WorkingHourDTO> results) {
        this.results = results;
    }
}
