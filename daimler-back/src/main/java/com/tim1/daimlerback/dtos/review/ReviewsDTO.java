package com.tim1.daimlerback.dtos.review;

import java.util.List;

public class ReviewsDTO {
    private Integer id;
    private Integer totalCount;
    private List<ReviewDTO> results;
    public ReviewsDTO() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<ReviewDTO> getResults() {
        return results;
    }

    public void setResults(List<ReviewDTO> results) {
        this.results = results;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
