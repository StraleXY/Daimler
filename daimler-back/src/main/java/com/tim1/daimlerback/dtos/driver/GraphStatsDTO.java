package com.tim1.daimlerback.dtos.driver;

import java.util.ArrayList;
import java.util.List;

public class GraphStatsDTO {
    private Integer totalRides;
    private Integer totalDistance;
    private List<Integer> ridesPerDay;
    private List<Integer> distancePerDay;

    public GraphStatsDTO() {
        this.ridesPerDay = new ArrayList<>();
        this.distancePerDay = new ArrayList<>();
        this.totalRides = 0;
        this.totalDistance = 0;
    }

    public GraphStatsDTO(GraphStatsDTO base) {
        this.totalRides = base.totalRides;
        this.totalDistance = base.totalDistance;
        this.ridesPerDay = base.ridesPerDay;
        this.distancePerDay = base.distancePerDay;
    }

    public Integer getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(Integer totalRides) {
        this.totalRides = totalRides;
    }

    public Integer getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Integer totalDistance) {
        this.totalDistance = totalDistance;
    }

    public List<Integer> getRidesPerDay() {
        return ridesPerDay;
    }

    public void setRidesPerDay(List<Integer> ridesPerDay) {
        this.ridesPerDay = ridesPerDay;
    }

    public List<Integer> getDistancePerDay() {
        return distancePerDay;
    }

    public void setDistancePerDay(List<Integer> distancePerDay) {
        this.distancePerDay = distancePerDay;
    }

    public void addDistanceDay(Integer distance) {
        this.distancePerDay.add(distance);
        this.totalDistance += distance;
    }

    public void addRideDay(Integer rides) {
        this.ridesPerDay.add(rides);
        this.totalRides += rides;
    }
}
