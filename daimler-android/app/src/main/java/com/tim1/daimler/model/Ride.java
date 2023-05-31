package com.tim1.daimler.model;

public class Ride {
    public enum Status {
        WAITING,
        ACCEPTED,
        REJECTED,
        ACTIVE,
        FINISHED
    }

    private Status status;
    private Route route;
    private String date;
    private String beginTime;
    private String finishTime;
    private float price;
    private float estimatedTime;
    private boolean panicPressed;
    private boolean transportingBabies;
    private boolean transportingPets;
    private boolean splitFare;

    public Ride(Status status, Route route, String date, String beginTime, String finishTime, float price,
                float estimatedTime, boolean panicPressed, boolean transportingBabies,
                boolean transportingPets, boolean splitFare) {
        this.status = status;
        this.route = route;
        this.date = date;
        this.beginTime = beginTime;
        this.finishTime = finishTime;
        this.price = price;
        this.estimatedTime = estimatedTime;
        this.panicPressed = panicPressed;
        this.transportingBabies = transportingBabies;
        this.transportingPets = transportingPets;
        this.splitFare = splitFare;
    }

    public Status getStatus() {
        return status;
    }

    public Route getRoute() {
        return route;
    }

    public String getSourceAddress() {
        return route.getSourceAddress();
    }

    public String getDestinationAddress() {
        return route.getDestinationAddress();
    }

    public float getLength() {
        return route.getLength();
    }

    public String getDate() {
        return date;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public float getPrice() {
        return price;
    }

    public float getEstimatedTime() {
        return estimatedTime;
    }

    public boolean isPanicPressed() {
        return panicPressed;
    }

    public boolean isTransportingBabies() {
        return transportingBabies;
    }

    public boolean isTransportingPets() {
        return transportingPets;
    }

    public boolean isSplitFare() {
        return splitFare;
    }
}
