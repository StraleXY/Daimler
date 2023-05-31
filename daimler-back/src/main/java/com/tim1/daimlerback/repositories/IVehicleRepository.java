package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IVehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findByDriver_Id(Integer id);
}
