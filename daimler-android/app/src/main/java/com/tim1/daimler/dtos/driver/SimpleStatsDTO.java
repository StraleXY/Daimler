package com.tim1.daimler.dtos.driver;

public class SimpleStatsDTO {
    private Integer earned;
    private Integer accepted;
    private Integer rejected;
    private Integer workingHours;

    public Integer getEarned() {
        return earned;
    }

    public void setEarned(Integer earned) {
        this.earned = earned;
    }

    public Integer getAccepted() {
        return accepted;
    }

    public void setAccepted(Integer accepted) {
        this.accepted = accepted;
    }

    public Integer getRejected() {
        return rejected;
    }

    public void setRejected(Integer rejected) {
        this.rejected = rejected;
    }

    public Integer getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(Integer workingHours) {
        this.workingHours = workingHours;
    }
}
