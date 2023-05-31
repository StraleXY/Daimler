package com.tim1.daimlerback.utils;

import com.tim1.daimlerback.entities.Passenger;
import com.tim1.daimlerback.entities.User;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class RecoveryToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private LocalDate expiryDate;

    private LocalDate calculateExpiryDate() {
        LocalDate date = LocalDate.now();
        // Expiry time is one year for now
        return date.plusYears(1);
    }

    public RecoveryToken() {

    }

    public RecoveryToken(User user, String token) {
        this.user = user;
        this.token = token;
        this.expiryDate = LocalDate.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}
