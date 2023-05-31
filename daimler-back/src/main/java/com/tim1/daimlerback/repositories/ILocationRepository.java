package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILocationRepository extends JpaRepository<Location, Integer> {
}
