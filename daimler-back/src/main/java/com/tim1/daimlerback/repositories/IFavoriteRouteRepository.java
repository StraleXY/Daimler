package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.entities.FavoriteRoute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IFavoriteRouteRepository extends JpaRepository<FavoriteRoute, Integer> {

    List<FavoriteRoute> findAllByPassenger_Id(Integer id);
    Optional<FavoriteRoute> findByDeparture_IdAndDestination_Id(double departureIde, double destinationId);
}
