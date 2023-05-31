package com.tim1.daimlerback.controllers;

import com.tim1.daimlerback.dtos.common.RegisterDTO;
import com.tim1.daimlerback.dtos.common.RidesDTO;
import com.tim1.daimlerback.dtos.passenger.FavoriteRouteDTO;
import com.tim1.daimlerback.dtos.passenger.PassengerDTO;
import com.tim1.daimlerback.dtos.passenger.PassengersDTO;
import com.tim1.daimlerback.dtos.passenger.UpdateUserDTO;
import com.tim1.daimlerback.dtos.ride.CreatedRideDTO;
import com.tim1.daimlerback.dtos.ride.InvitationDTO;
import com.tim1.daimlerback.dtos.ride.InvitationResponseDTO;
import com.tim1.daimlerback.entities.Passenger;
import com.tim1.daimlerback.entities.User;
import com.tim1.daimlerback.entities.enumeration.ERole;
import com.tim1.daimlerback.services.PassengerService;
import com.tim1.daimlerback.utils.OnRegistrationCompleteEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/passenger")
public class PassengerController {
    @Autowired
    private PassengerService passengerService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    // {passengerId}/favourite
    // {passengerId}/favourite
    // {passengerId}/favourite/{id}

    @GetMapping("{id}")
    //@PreAuthorize("hasRole('ROLE_PASSENGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<PassengerDTO> getPassenger(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        //if (user.getRole() != ERole.ROLE_ADMIN && !id.equals(user.getId())) {
        //    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        //}
        return new ResponseEntity<>(new PassengerDTO(passengerService.getPassenger(id)), HttpStatus.OK);
    }

    @PutMapping(value = "{id}", consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_PASSENGER')")
    public ResponseEntity<PassengerDTO> updatePassenger(@PathVariable Integer id, @RequestBody UpdateUserDTO passengerDTO, @AuthenticationPrincipal User user) {
        if (!id.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        Passenger passenger = passengerService.update(id, passengerDTO);
        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<PassengerDTO> createPassenger(@RequestBody RegisterDTO registerDTO) {
        Passenger passenger = passengerService.register(registerDTO);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(passenger));
        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
    }

    @GetMapping(value="/activate/{activationId}")
    public ResponseEntity<PassengerDTO> activate(@PathVariable String activationId) {
        passengerService.verify(activationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or hasRole('ROLE_PASSENGER'))")
    public ResponseEntity<PassengersDTO> getAllPassengers(@RequestParam Integer page, @RequestParam Integer size) {
        PassengersDTO dto = passengerService.getAllPassengers(page, size);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("{id}/ride")
    @PreAuthorize("hasRole('ROLE_PASSENGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<RidesDTO> getPassengerRides(@PathVariable Integer id,
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
        dto.setResults(passengerService.getRides(id, --page, size, sort).stream().map(CreatedRideDTO::new).collect(Collectors.toList()));
        dto.setTotalCount(dto.getResults().size());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(value = "invitationResponse", consumes = "application/json")
    public ResponseEntity<Void> inviteHandler(@RequestBody InvitationResponseDTO dto) {
        passengerService.handleInvitationResponse(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "invite", consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_PASSENGER')")
    public ResponseEntity<Void> invite(@RequestBody InvitationDTO dto) {
        passengerService.invite(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}