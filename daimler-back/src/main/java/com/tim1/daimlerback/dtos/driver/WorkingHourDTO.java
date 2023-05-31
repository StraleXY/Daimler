package com.tim1.daimlerback.dtos.driver;

import com.tim1.daimlerback.entities.Ride;
import com.tim1.daimlerback.entities.WorkingHour;

public class WorkingHourDTO {
    private Integer id;
    private String start;
    private String end;

    public WorkingHourDTO() {

    }

    public WorkingHourDTO(WorkingHour workingHour) {
        this.id = workingHour.getId();
        this.start = workingHour.getStart().toString();
        if (workingHour.getEnd() == null) {
            this.end = null;
        } else {
            this.end = workingHour.getEnd().toString();
        }
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
