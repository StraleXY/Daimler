package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.entities.DriverReview;
import com.tim1.daimlerback.entities.VehicleReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface IDriverReviewRepository extends JpaRepository<DriverReview, Integer> {

    @Query("SELECT dr FROM DriverReview dr JOIN dr.driver d WHERE d.id = :id")
    public List<DriverReview> findAllByDriverId(Integer id);

    @Query("SELECT dr FROM DriverReview dr JOIN dr.ride r WHERE r.id = :id")
    public List<DriverReview> findAllByRideId(Integer id);


}
