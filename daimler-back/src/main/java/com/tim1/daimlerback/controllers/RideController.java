package com.tim1.daimlerback.controllers;

import com.tim1.daimlerback.dtos.common.*;
import com.tim1.daimlerback.dtos.driver.DriverShortDTO;
import com.tim1.daimlerback.dtos.passenger.FavoriteRouteDTO;
import com.tim1.daimlerback.dtos.passenger.PassengerShortDTO;
import com.tim1.daimlerback.dtos.ride.CreateRideDTO;
import com.tim1.daimlerback.dtos.ride.CreatedRideDTO;
import com.tim1.daimlerback.dtos.panic.PanicRideDTO;
import com.tim1.daimlerback.dtos.ride.InvitationDTO;
import com.tim1.daimlerback.dtos.ride.InvitationResponseDTO;
import com.tim1.daimlerback.dtos.user.UserInRideDTO;
import com.tim1.daimlerback.entities.Ride;
import com.tim1.daimlerback.entities.User;
import com.tim1.daimlerback.entities.enumeration.ERole;
import com.tim1.daimlerback.services.RideService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/ride")
public class RideController {

    @Autowired
    private RideService rideService;

    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_PASSENGER')")
    public ResponseEntity<CreatedRideDTO> createRide(@RequestBody CreateRideDTO dto) {
        CreatedRideDTO ret = rideService.createRide(dto);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("driver/{driverId}/active")
    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CreatedRideDTO> getDriverActiveRide(@PathVariable Integer driverId, @AuthenticationPrincipal User user) {
        if (user.getRole() != ERole.ROLE_ADMIN && !driverId.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        return new ResponseEntity<>(new CreatedRideDTO(rideService.getDriverActiveRide(driverId)), HttpStatus.OK);
    }

    @GetMapping("passenger/{passengerId}/active")
    @PreAuthorize("hasRole('ROLE_PASSENGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CreatedRideDTO> getPassengerActiveRide(@PathVariable Integer passengerId, @AuthenticationPrincipal User user) {
        if (user.getRole() != ERole.ROLE_ADMIN && !passengerId.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        Ride ride = rideService.getPassengerActiveRide(passengerId);
        if (ride != null) return new ResponseEntity<>(new CreatedRideDTO(ride), HttpStatus.OK);
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger has no active rides!");
    }

    @GetMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CreatedRideDTO> getRideDetails(@PathVariable Integer id) {
        return new ResponseEntity<>(new CreatedRideDTO(rideService.get(id)), HttpStatus.OK);
    }

    @PutMapping("{id}/withdraw")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_PASSENGER')")
    public ResponseEntity<CreatedRideDTO> withdrawRide(@PathVariable Integer id) {
        CreatedRideDTO ret = rideService.withdraw(id);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @PutMapping(value = "{id}/panic", consumes = "application/json")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_PASSENGER') or hasRole('ROLE_DRIVER')")
    public ResponseEntity<PanicRideDTO> panicRide(@RequestBody ReasonDTO reasonDto, @PathVariable Integer id, @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(rideService.panic(reasonDto, id, user.getId()), HttpStatus.OK);
    }

    @PutMapping("{id}/accept")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ResponseEntity<CreatedRideDTO> acceptRide(@PathVariable Integer id) {
        CreatedRideDTO ret = rideService.acceptRide(id);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @PutMapping("{id}/end")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ResponseEntity<CreatedRideDTO> endRide(@PathVariable Integer id) {
        CreatedRideDTO ret = rideService.endRide(id);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @PutMapping("{id}/start")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ResponseEntity<CreatedRideDTO> startRide(@PathVariable Integer id) {
        CreatedRideDTO ret = rideService.startRide(id);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @PutMapping(value = "{id}/cancel", consumes = "application/json")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_DRIVER')")
    public ResponseEntity<CreatedRideDTO> cancelRide(@PathVariable Integer id, @RequestBody ReasonDTO reasonDto) {
        CreatedRideDTO ret = rideService.cancelRide(id, reasonDto);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @PostMapping("{passenger_id}/favorite")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_PASSENGER')")
    public ResponseEntity<FavoriteRouteDTO> addRouteToFavorite(@PathVariable Integer passenger_id, @RequestParam Integer departureId, @RequestParam Integer destinationId, @AuthenticationPrincipal User user) {
        if (!passenger_id.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        FavoriteRouteDTO dto = new FavoriteRouteDTO(rideService.insertFavoriteRoute(passenger_id, departureId, destinationId));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("{passenger_id}/favorite/{id}")
    @PreAuthorize("hasRole('ROLE_PASSENGER')")
    public ResponseEntity<FavoriteRouteDTO> deleteFavorite(@PathVariable Integer passenger_id, @PathVariable Integer id, @AuthenticationPrincipal User user) {
        if (!passenger_id.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        FavoriteRouteDTO dto = new FavoriteRouteDTO(rideService.deleteFavoriteRoute(passenger_id, id));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("{passenger_id}/favorite")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_PASSENGER')")
    public ResponseEntity<List<FavoriteRouteDTO>> getFavouriteRoutes(@PathVariable Integer passenger_id, @AuthenticationPrincipal User user) {
        if (!passenger_id.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        List<FavoriteRouteDTO> routes = rideService.getFavoriteRoutes(passenger_id).stream().map(FavoriteRouteDTO::new).toList();
        if (routes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No favourite routes found!");
        }
        return new ResponseEntity<>(routes, HttpStatus.OK);
    }
}
