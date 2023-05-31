package com.tim1.daimlerback.controllers;

import com.tim1.daimlerback.dtos.common.LocationDTO;
import com.tim1.daimlerback.dtos.vehicle.VehicleLocationsDTO;
import com.tim1.daimlerback.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/vehicle")
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;

    @PutMapping(value = "{id}/location", consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ResponseEntity<String> updateDriver(@RequestBody LocationDTO locationDto, @PathVariable Integer id) {
        try {
            boolean found = vehicleService.updateLocation(locationDto, id);
            if (found)
                return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
            else
                return new ResponseEntity<String>("Vehicle does not exist!", HttpStatus.NOT_FOUND);
        } catch(Exception ex) {
            return new ResponseEntity<String>("Bad input data", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<VehicleLocationsDTO> getLocations() {
        VehicleLocationsDTO dto = vehicleService.getLocations();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
