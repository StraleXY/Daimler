package com.tim1.daimler.model;

public class Route {

    private String sourceAddress;
    private String destinationAddress;
    private float length;

    public Route(String sourceAddress, String destinationAddress, float length) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.length = length;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public float getLength() {
        return length;
    }
}
