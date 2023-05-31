package com.tim1.daimler.dtos.user;


public class UserShortDTO {

    private Integer id;
    private String email;

    public UserShortDTO() {

    }

    public UserShortDTO(int id, String email) {
        this.id = id;
        this.email = email;
    }

    public UserShortDTO(UserDTO passenger) {
        this.id = passenger.getId();
        this.email = passenger.getEmail();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
