package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPassengerRepository extends JpaRepository<Passenger, Integer> {
    public Optional<Passenger> findByEmail(String email);
    List<Passenger> findAll();
}
