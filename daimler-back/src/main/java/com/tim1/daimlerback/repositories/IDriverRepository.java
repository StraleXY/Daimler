package com.tim1.daimlerback.repositories;


import com.tim1.daimlerback.entities.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IDriverRepository extends JpaRepository<Driver, Integer> {
    Optional<Driver> findByEmail(String email);

    List<Driver> findAllByIsBlockedAndIsActive(boolean blocked, boolean active);
}
