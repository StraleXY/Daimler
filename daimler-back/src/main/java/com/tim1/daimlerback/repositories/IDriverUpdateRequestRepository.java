package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.entities.Driver;
import com.tim1.daimlerback.entities.DriverUpdateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IDriverUpdateRequestRepository extends JpaRepository<DriverUpdateRequest, Integer> {
    Optional<DriverUpdateRequest> findByDriverId(Integer id);
}
