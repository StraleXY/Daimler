package com.tim1.daimlerback.repositories;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tim1.daimlerback.entities.Passenger;
import com.tim1.daimlerback.entities.Ride;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class IRideRepositoryTest {
    @Autowired
    IRideRepository rideRepository;

    // --------------------------------------------- findRideByPassengerId ------------------------------------------------- //

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {Integer.MAX_VALUE, Integer.MIN_VALUE, 13435})
    void findRideByInvalidPassengerIdTest(Integer passengerId) {
        Pageable pageable = Pageable.ofSize(5);
        Page<Ride> rides = rideRepository.findRideByPassengerId(passengerId, pageable);
        Assertions.assertEquals(0, rides.getNumberOfElements());
    }

    @Test
    void findRideByValidPassengerIdTest() {
        Pageable pageable = Pageable.ofSize(5);
        Page<Ride> rides = rideRepository.findRideByPassengerId(20, pageable);
        Assertions.assertEquals(1, rides.getNumberOfElements());
        Ride ride = rides.getContent().get(0);
        Assertions.assertEquals(50, ride.getId());
        Assertions.assertEquals("PENDING", ride.getStatus());
        Assertions.assertTrue(ride.getBabyTransport());
        Assertions.assertFalse(ride.getPetTransport());
        Assertions.assertEquals(500, ride.getTotalCost());
        Assertions.assertEquals(200.4, ride.getDistance());
    }

    // --------------------------------------------- findPendingRideByPassengerId ------------------------------------------------- //

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {Integer.MAX_VALUE, Integer.MIN_VALUE, 13435})
    void findPendingRideByInvalidPassengerIdTest(Integer passengerId) {
        List<Ride> rides = rideRepository.findPendingRideByPassengerId(passengerId);
        Assertions.assertEquals(0, rides.size());
    }

    @Test
    void findPendingRideByValidPassengerIdTest() {
        List<Ride> rides = rideRepository.findPendingRideByPassengerId(20);
        Assertions.assertEquals(1, rides.size());
        Ride ride = rides.get(0);
        Assertions.assertEquals(50, ride.getId());
        Assertions.assertEquals("PENDING", ride.getStatus());
        Assertions.assertTrue(ride.getBabyTransport());
        Assertions.assertFalse(ride.getPetTransport());
        Assertions.assertEquals(500, ride.getTotalCost());
        Assertions.assertEquals(200.4, ride.getDistance());
    }

    // --------------------------------------------- findPendingRideByDriverId ------------------------------------------------- //

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {Integer.MAX_VALUE, Integer.MIN_VALUE, 13435})
    void findPendingRideByInvalidDriverIdTest(Integer driverId) {
        List<Ride> rides = rideRepository.findPendingRideByDriverId(driverId);
        Assertions.assertEquals(0, rides.size());
    }

    @Test
    void findPendingRideByValidDriverIdTest() {
        List<Ride> rides = rideRepository.findPendingRideByDriverId(21);
        Assertions.assertEquals(1, rides.size());
        Ride ride = rides.get(0);
        Assertions.assertEquals(50, ride.getId());
        Assertions.assertEquals("PENDING", ride.getStatus());
        Assertions.assertTrue(ride.getBabyTransport());
        Assertions.assertFalse(ride.getPetTransport());
        Assertions.assertEquals(500, ride.getTotalCost());
        Assertions.assertEquals(200.4, ride.getDistance());
    }

    // --------------------------------------- findFinishedByPassengerId -------------------------------------------- //

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {Integer.MAX_VALUE, Integer.MIN_VALUE, 13435})
    void findFinishedByInvalidPassengerId(Integer passengerId) {
        List<Ride> rides = rideRepository.findFinishedByPassengerId(passengerId, Integer.MIN_VALUE, Integer.MAX_VALUE);
        Assertions.assertEquals(0, rides.size());
    }

    @Test
    void findFinishedByValidPassengerId() {
        int rideId = 666;
        int passengerId = 666;
        int driverId = 6666;

        List<Ride> rides = rideRepository.findFinishedByPassengerId(passengerId, Integer.MIN_VALUE, Integer.MAX_VALUE);
        Assertions.assertEquals(1, rides.size());
        Ride ride = rides.get(0);
        Assertions.assertEquals(rideId, ride.getId());
        Assertions.assertEquals(driverId, ride.getDriver().getId());
        Assertions.assertEquals(passengerId, ride.getPassengers().stream().collect(Collectors.toList()).get(0).getId());
        Assertions.assertEquals(true, ride.getBabyTransport());
        Assertions.assertEquals(false, ride.getPetTransport());
        Assertions.assertEquals(6000, ride.getTotalCost());
        Assertions.assertEquals(600, ride.getDistance());
    }

    // ---------------------------------------- findFinishedByDriverId ---------------------------------------------- //

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {Integer.MAX_VALUE, Integer.MIN_VALUE, 13435})
    void findFinishedByInvalidDriverId(Integer driverId) {
        List<Ride> rides = rideRepository.findFinishedByDriverId(driverId, Integer.MIN_VALUE, Integer.MAX_VALUE);
        Assertions.assertEquals(0, rides.size());
    }

    @Test
    void findFinishedByValidDriverId() {
        int rideId = 666;
        int passengerId = 666;
        int driverId = 6666;

        List<Ride> rides = rideRepository.findFinishedByDriverId(driverId, Integer.MIN_VALUE, Integer.MAX_VALUE);
        Assertions.assertEquals(1, rides.size());
        Ride ride = rides.get(0);
        Assertions.assertEquals(rideId, ride.getId());
        Assertions.assertEquals(driverId, ride.getDriver().getId());
        Assertions.assertEquals(passengerId, ride.getPassengers().stream().collect(Collectors.toList()).get(0).getId());
        Assertions.assertEquals(true, ride.getBabyTransport());
        Assertions.assertEquals(false, ride.getPetTransport());
        Assertions.assertEquals(6000, ride.getTotalCost());
        Assertions.assertEquals(600, ride.getDistance());
    }

    // ---------------------------------------- findRejectedByDriverId ---------------------------------------------- //

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {Integer.MAX_VALUE, Integer.MIN_VALUE, 13435})
    void findRejectedByInvalidDriverId(Integer driverId) {
        List<Ride> rides = rideRepository.findRejectedByDriverId(driverId, Integer.MIN_VALUE, Integer.MAX_VALUE);
        Assertions.assertEquals(0, rides.size());
    }

    @Test
    void findRejectedByValidDriverId() {
        int rideId = 6666;
        int passengerId = 666;
        int driverId = 6666;

        List<Ride> rides = rideRepository.findRejectedByDriverId(driverId, Integer.MIN_VALUE, Integer.MAX_VALUE);
        Assertions.assertEquals(1, rides.size());
        Ride ride = rides.get(0);
        Assertions.assertEquals(rideId, ride.getId());
        Assertions.assertEquals(driverId, ride.getDriver().getId());
        Assertions.assertEquals(passengerId, ride.getPassengers().stream().collect(Collectors.toList()).get(0).getId());
        Assertions.assertEquals(true, ride.getBabyTransport());
        Assertions.assertEquals(false, ride.getPetTransport());
        Assertions.assertEquals(6000, ride.getTotalCost());
        Assertions.assertEquals(600, ride.getDistance());
    }

    // ------------------------------------------ findByDriverId ------------------------------------------------ //
    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {Integer.MAX_VALUE, Integer.MIN_VALUE, 555})
    void findRideByInvalidDriverId(Integer driverId) {
        Page<Ride> rides = rideRepository.findByDriverId(driverId, PageRequest.of(1, 1));
        Assertions.assertEquals(0, rides.getTotalElements());
    }

    @Test
    void findRideByValidDriverId() {
        int driverId = 6666;

        Page<Ride> rides = rideRepository.findByDriverId(driverId, PageRequest.of(1, 2));
        Assertions.assertEquals(2, rides.getTotalElements());
        for(Ride ride : rides.getContent()) {
            Assertions.assertEquals(driverId, ride.getDriver().getId());
        }
    }

    // -------------------------------------------- findByPassengerId -------------------------------------------------- //
    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {Integer.MAX_VALUE, Integer.MIN_VALUE, 555})
    void findRideByInvalidPassengerId(Integer passengerId) {
        Page<Ride> rides = rideRepository.findByPassengerId(passengerId, PageRequest.of(1, 1));
        Assertions.assertEquals(0, rides.getTotalElements());
    }

    @Test
    void findRideByValidPassengerId() {
        int passengerId = 666;

        Page<Ride> rides = rideRepository.findByPassengerId(passengerId, PageRequest.of(1, 2));
        Assertions.assertEquals(2, rides.getTotalElements());
        for(Ride ride : rides.getContent()) {
            Assertions.assertTrue(ride.getPassengers().stream().map(Passenger::getId).toList().contains(passengerId));
        }
    }

    // --------------------------------------------- findByStatus --------------------------------------------------- //
    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "REJECTED"})
    void findByStatusValidTest(String status) {
        List<Ride> rides = rideRepository.findByStatus(status);
        Assertions.assertEquals(1, rides.size());
        Assertions.assertEquals(status, rides.get(0).getStatus());
    }

}