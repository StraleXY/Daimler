package com.tim1.daimler.dtos.user;

import com.tim1.daimler.util.ERole;

public class TokenDTO {
    private String accessToken;
    private String refreshToken;
    private Integer userId;
    private ERole userRole;

    public TokenDTO() {

    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public ERole getUserRole() {
        return userRole;
    }

    public void setUserRole(ERole userRole) {
        this.userRole = userRole;
    }
}
