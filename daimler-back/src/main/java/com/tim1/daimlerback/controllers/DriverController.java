package com.tim1.daimlerback.controllers;

import com.tim1.daimlerback.dtos.common.RegisterDTO;
import com.tim1.daimlerback.dtos.common.RidesDTO;
import com.tim1.daimlerback.dtos.driver.*;
import com.tim1.daimlerback.dtos.passenger.UpdateUserDTO;
import com.tim1.daimlerback.dtos.ride.CreatedRideDTO;
import com.tim1.daimlerback.dtos.vehicle.VehicleRegisterDTO;
import com.tim1.daimlerback.dtos.vehicle.VehicleRegisteredDTO;
import com.tim1.daimlerback.entities.Driver;
import com.tim1.daimlerback.entities.User;
import com.tim1.daimlerback.entities.Vehicle;
import com.tim1.daimlerback.entities.enumeration.ERole;
import com.tim1.daimlerback.services.DriverService;
import com.tim1.daimlerback.services.UpdateRequestService;
import com.tim1.daimlerback.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/driver")
public class DriverController {
    @Autowired
    private DriverService driverService;
    @Autowired
    private UpdateRequestService requestService;
    @Autowired
    private VehicleService vehicleService;

    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DriverDTO> registerDriver(@RequestBody RegisterDTO registerDTO) {
        Driver driver = driverService.register(registerDTO);
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @PostMapping(value="/{id}/vehicle", consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<VehicleRegisteredDTO> registerVehicle(@PathVariable Integer id, @RequestBody VehicleRegisterDTO registerDTO, @AuthenticationPrincipal User user) {
        if (user.getRole() != ERole.ROLE_ADMIN && !id.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        return new ResponseEntity<>(new VehicleRegisteredDTO(vehicleService.register(id, registerDTO)), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DriversDTO> getAllDrivers(@RequestParam Integer page, @RequestParam Integer size) {
        DriversDTO dto = driverService.getAllDrivers();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("{id}")
    // @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<DriverDTO> getDriver(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        // if (user.getRole() != ERole.ROLE_ADMIN && !id.equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        return new ResponseEntity<>(new DriverDTO(driverService.getDriver(id)), HttpStatus.OK);
    }

    @GetMapping("requests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<DriverDTO>> getUpdateRequests() {
        return new ResponseEntity<>(requestService.get().stream().map(DriverDTO::new).collect(Collectors.toList()), HttpStatus.OK);
    }

    @DeleteMapping("request/{driverId}")
    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRequest(@PathVariable Integer driverId, @AuthenticationPrincipal User user) {
        if (user.getRole() != ERole.ROLE_ADMIN && !driverId.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        requestService.delete(driverId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "{id}", consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ResponseEntity<DriverDTO> requestUpdateDriver(@PathVariable Integer id, @RequestBody UpdateUserDTO driverDTO, @AuthenticationPrincipal User user) {
        if (!id.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        requestService.insert(id, driverDTO);
        return new ResponseEntity<>(new DriverDTO(id, driverDTO), HttpStatus.OK);
    }

    @GetMapping("{id}/documents")
    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<DocumentDTO>> getDocuments(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        if (user.getRole() != ERole.ROLE_ADMIN && !id.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        return new ResponseEntity<>(driverService.getDocuments(id), HttpStatus.OK);
    }

    @DeleteMapping("document/{document-id}")
    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteDocument(@PathVariable("document-id") Integer documentId) {
        driverService.deleteDocument(documentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value="{id}/documents", consumes="application/json")
    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<DocumentDTO> createDocument(@PathVariable Integer id, @RequestBody CreateDocumentDTO document) {
        DocumentDTO dto = driverService.createDocument(id, document);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(value="{id}/vehicle")
    // @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<VehicleRegisteredDTO> getVehicle(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        // if (user.getRole() != ERole.ROLE_ADMIN &&!id.equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        VehicleRegisteredDTO vehicle = new VehicleRegisteredDTO(vehicleService.getDriversVehicle(id));
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @PutMapping(value="/{id}/vehicle", consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<VehicleRegisteredDTO> updateVehicle(@PathVariable Integer id, @RequestBody VehicleRegisterDTO registerDTO, @AuthenticationPrincipal User user) {
        if (user.getRole() != ERole.ROLE_ADMIN && !id.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        VehicleRegisteredDTO vehicle = new VehicleRegisteredDTO(vehicleService.updateDriversVehicle(id, registerDTO));
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @GetMapping(value="{id}/working-hour")
    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<WorkingHoursDTO> getWorkingHours(@PathVariable Integer id,
                                                           @RequestParam Integer page,
                                                           @RequestParam Integer size,
                                                           @AuthenticationPrincipal User user) {
        if (user.getRole() != ERole.ROLE_ADMIN && !id.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }

        WorkingHoursDTO dto = new WorkingHoursDTO();
        //ArrayList<WorkingHourDTO> results = new ArrayList<WorkingHourDTO>();
        //results.add(new WorkingHourDTO());
        ArrayList<WorkingHourDTO> results = driverService.getWorkingHours(id, page, size);
        dto.setResults(results);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(value="/{id}/working-hour", consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<WorkingHourDTO> createWorkingHours(@PathVariable Integer id, @RequestBody CreateWorkingHourDTO dto, @AuthenticationPrincipal User user) {
        if (user.getRole() != ERole.ROLE_ADMIN && !id.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        return new ResponseEntity<>(new WorkingHourDTO(driverService.createWorkingHour(id, dto.getStart())), HttpStatus.OK);
    }

    @GetMapping(value="{id}/ride")
    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<RidesDTO> getRides(@PathVariable Integer id,
                                             @RequestParam Integer page,
                                             @RequestParam Integer size,
                                             @RequestParam String sort,
                                             @RequestParam String from,
                                             @RequestParam String to,
                                             @AuthenticationPrincipal User user) {
        if (user.getRole() != ERole.ROLE_ADMIN && !id.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }

        RidesDTO dto = new RidesDTO(1);
        dto.setResults(driverService.getRides(id, --page, size, sort).stream().map(CreatedRideDTO::new).collect(Collectors.toList()));
        dto.setTotalCount(dto.getResults().size());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(value="/working-hour/{working-hour-id}")
    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<WorkingHourDTO> getWorkingHourDetails(@PathVariable("working-hour-id") Integer workingHourId) {
        return new ResponseEntity<>(new WorkingHourDTO(driverService.getWorkingHour(workingHourId)), HttpStatus.OK);
    }

    @PutMapping(value="/working-hour/{working-hour-id}", consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<WorkingHourDTO> putWorkingHourDetails(@PathVariable("working-hour-id") Integer workingHourId, @RequestBody UpdateWorkingHourDTO dto, @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(new WorkingHourDTO(driverService.updateWorkingHour(workingHourId, dto.getEnd(), user)), HttpStatus.OK);
    }

    @GetMapping(value = "stats/simple/{id}/{from}/{to}")
    public ResponseEntity<SimpleStatsDTO> getSimpleStats(@PathVariable Integer id, @PathVariable Long from, @PathVariable Long to) {
        return new ResponseEntity<>(driverService.getSimpleStats(id, from, to), HttpStatus.OK);
    }

    @GetMapping(value = "stats/graph/{id}/{from}/{to}")
    public ResponseEntity<GraphStatsDTO> getGraphStats(@PathVariable Integer id, @PathVariable Long from, @PathVariable Long to) {
        return new ResponseEntity<>(driverService.getGraphStats(id, from, to), HttpStatus.OK);
    }
}
