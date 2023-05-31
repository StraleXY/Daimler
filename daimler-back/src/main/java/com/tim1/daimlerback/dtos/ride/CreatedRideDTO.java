package com.tim1.daimlerback.dtos.ride;

import com.tim1.daimlerback.dtos.common.DepartureDestinationDTO;
import com.tim1.daimlerback.dtos.common.LocationDTO;
import com.tim1.daimlerback.dtos.common.RejectionDTO;
import com.tim1.daimlerback.dtos.user.UserInRideDTO;
import com.tim1.daimlerback.entities.Location;
import com.tim1.daimlerback.entities.Ride;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CreatedRideDTO {
    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    private Integer id;
    private String startTime;
    private String endTime;
    private Integer totalCost;
    private UserInRideDTO driver;
    private List<UserInRideDTO> passengers;
    private Integer estimatedTimeInMinutes;
    private String vehicleType;
    private Boolean petTransport;
    private Boolean babyTransport;
    private RejectionDTO rejection;
    private List<DepartureDestinationDTO> locations;
    private String status;
    private Double distance;
    private Long scheduledTimestamp;
    private String scheduledTime;
    public CreatedRideDTO() {

    }
    public CreatedRideDTO(Ride ride) {
        id = ride.getId();
        startTime = ride.getStartTime();
        endTime = ride.getEndTime();
        totalCost = ride.getTotalCost();
        driver = ride.getDriver() == null ? null : new UserInRideDTO(ride.getDriver());
        passengers = ride.getPassengers().stream().map(UserInRideDTO::new).collect(Collectors.toList());
        estimatedTimeInMinutes = ride.getEstimatedTimeInMinutes();
        vehicleType = ride.getVehicleType();
        petTransport = ride.getPetTransport();
        babyTransport = ride.getBabyTransport();
        rejection = null; //TODO Fix this
        locations = generateDepartures(ride.getLocations());
        status = ride.getStatus();
        distance = ride.getDistance();
        scheduledTimestamp = ride.getScheduledTimestamp();
        if (ride.getScheduledTimestamp() != null)
        scheduledTime = ride.getScheduledTimestamp().toString();
    }
    private List<DepartureDestinationDTO> generateDepartures(Collection<Location> locations) {
        List<DepartureDestinationDTO> departures = new ArrayList<>();
        for (int i = 1; i < locations.size(); i++) departures.add(new DepartureDestinationDTO((Location) locations.toArray()[i - 1], (Location) locations.toArray()[i]));
        return departures;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public Integer getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }
    public UserInRideDTO getDriver() {
        return driver;
    }
    public void setDriver(UserInRideDTO driver) {
        this.driver = driver;
    }
    public List<UserInRideDTO> getPassengers() {
        return passengers;
    }
    public void setPassengers(List<UserInRideDTO> passengers) {
        this.passengers = passengers;
    }
    public Integer getEstimatedTimeInMinutes() {
        return estimatedTimeInMinutes;
    }
    public void setEstimatedTimeInMinutes(Integer estimatedTimeInMinutes) {
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
    }
    public String getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    public Boolean getPetTransport() {
        return petTransport;
    }
    public void setPetTransport(Boolean petTransport) {
        this.petTransport = petTransport;
    }
    public Boolean getBabyTransport() {
        return babyTransport;
    }
    public void setBabyTransport(Boolean babyTransport) {
        this.babyTransport = babyTransport;
    }
    public RejectionDTO getRejection() {
        return rejection;
    }
    public void setRejection(RejectionDTO rejection) {
        this.rejection = rejection;
    }
    public List<DepartureDestinationDTO> getLocations() {
        return locations;
    }
    public void setLocations(List<DepartureDestinationDTO> locations) {
        this.locations = locations;
    }
    public void setLocationDTOs(List<LocationDTO> locations) {
        this.locations = new ArrayList<>();
        for(int i = 0; i < locations.size(); ++i) {
            if(i+1 == locations.size()) return;
            this.locations.add(new DepartureDestinationDTO(locations.get(i), locations.get(i+1)));
        }
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Double getDistance() {
        return distance;
    }
    public void setDistance(Double distance) {
        this.distance = distance;
    }
    public Long getScheduledTimestamp() {
        return scheduledTimestamp;
    }
    public void setScheduledTimestamp(Long scheduledTimestamp) {
        this.scheduledTimestamp = scheduledTimestamp;
    }

}
