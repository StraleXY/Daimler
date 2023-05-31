package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.entities.VehicleReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface IVehicleReviewRepository extends JpaRepository<VehicleReview, Integer> {

    @Query("SELECT vr FROM VehicleReview vr JOIN vr.vehicle v WHERE v.id = :id")
    public List<VehicleReview> findAllByVehicleId(Integer id);

    @Query("SELECT vr FROM VehicleReview vr JOIN vr.ride r WHERE r.id = :id")
    public List<VehicleReview> findAllByRideId(Integer id);
}
