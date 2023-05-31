package com.tim1.daimlerback.controllers;

import com.tim1.daimlerback.dtos.panic.PanicListDTO;
import com.tim1.daimlerback.dtos.panic.PanicRideDTO;
import com.tim1.daimlerback.dtos.ride.CreatedRideDTO;
import com.tim1.daimlerback.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "api/panic")
public class PanicController {
    @Autowired
    private RideService rideService;
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PanicListDTO> getPanicList() {

        return new ResponseEntity<>(rideService.getPanicList(), HttpStatus.OK);
    }
}
