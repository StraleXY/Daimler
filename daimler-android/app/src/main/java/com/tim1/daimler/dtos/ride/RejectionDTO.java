package com.tim1.daimler.dtos.ride;

public class RejectionDTO {
    private String reason;
    private String timeOfRejection;
    public RejectionDTO() {

    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTimeOfRejection() {
        return timeOfRejection;
    }

    public void setTimeOfRejection(String timeOfRejection) {
        this.timeOfRejection = timeOfRejection;
    }
}
