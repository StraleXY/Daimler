package com.tim1.daimlerback.dtos.user;

import com.tim1.daimlerback.dtos.driver.GraphStatsDTO;

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

    public UserGraphStatsDTO(GraphStatsDTO base) {
        super(base);
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

    public void addAmountDay(Integer amount) {
        this.amount += amount;
        this.amountPerDay.add(amount);
    }
}
