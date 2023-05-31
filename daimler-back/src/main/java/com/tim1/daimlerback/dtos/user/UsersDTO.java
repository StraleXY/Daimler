package com.tim1.daimlerback.dtos.user;

import com.tim1.daimlerback.dtos.passenger.PassengerDTO;

import java.util.List;

public class UsersDTO {
    private Integer totalCount;

    public List<UserDTO> getResults() {
        return results;
    }

    public void setResults(List<UserDTO> results) {
        this.results = results;
    }

    private List<UserDTO> results;

    public UsersDTO() {

    }

    public UsersDTO(List<UserDTO> users) {
        this.results = users;
        this.totalCount = users.size();
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
