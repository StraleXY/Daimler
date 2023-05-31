package com.tim1.daimlerback.repositories;


import com.tim1.daimlerback.entities.Location;
import com.tim1.daimlerback.entities.Rejection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRejectionRepository extends JpaRepository<Rejection, Integer> {
}
