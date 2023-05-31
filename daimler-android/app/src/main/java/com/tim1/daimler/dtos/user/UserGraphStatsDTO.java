package com.tim1.daimler.dtos.user;

import com.tim1.daimler.dtos.ride.GraphStatsDTO;

import java.util.ArrayList;
import java.util.List;

public class UserGraphStatsDTO extends GraphStatsDTO {

    private Integer amount;
    private List<Integer> amountPerDay;

    public UserGraphStatsDTO() {
        super();
        this.amount = 0;
        this.amountPerDay = new ArrayList<Integer>();
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public List<Integer> getAmountPerDay() {
        return amountPerDay;
    }

    public void setAmountPerDay(List<Integer> amountPerDay) {
        this.amountPerDay = amountPerDay;
    }
}
