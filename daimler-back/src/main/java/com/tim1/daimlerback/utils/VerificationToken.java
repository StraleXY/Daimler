package com.tim1.daimlerback.utils;

import com.tim1.daimlerback.entities.Passenger;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;

    @OneToOne(targetEntity = Passenger.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "passenger_id")
    private Passenger passenger;

    private LocalDate expiryDate;

    private LocalDate calculateExpiryDate() {
        LocalDate date = LocalDate.now();
        // Expiry time is one year for now
        return date.plusYears(1);
    }

    public VerificationToken() {

    }

    public VerificationToken(Passenger passenger, String token) {
        this.passenger = passenger;
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

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}