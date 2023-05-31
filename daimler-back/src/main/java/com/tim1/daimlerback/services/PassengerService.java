package com.tim1.daimlerback.services;

import com.tim1.daimlerback.dtos.common.RegisterDTO;
import com.tim1.daimlerback.dtos.passenger.FavoriteRouteDTO;
import com.tim1.daimlerback.dtos.passenger.PassengerDTO;
import com.tim1.daimlerback.dtos.passenger.PassengersDTO;
import com.tim1.daimlerback.dtos.ride.InvitationDTO;
import com.tim1.daimlerback.dtos.ride.InvitationResponseDTO;
import com.tim1.daimlerback.dtos.user.LoginDTO;
import com.tim1.daimlerback.dtos.passenger.UpdateUserDTO;
import com.tim1.daimlerback.entities.FavoriteRoute;
import com.tim1.daimlerback.entities.Location;
import com.tim1.daimlerback.entities.Passenger;
import com.tim1.daimlerback.entities.Ride;
import com.tim1.daimlerback.repositories.*;
import com.tim1.daimlerback.entities.enumeration.ERole;
import com.tim1.daimlerback.repositories.IPassengerRepository;
import com.tim1.daimlerback.repositories.IRideRepository;
import com.tim1.daimlerback.repositories.ITokenRepository;
import com.tim1.daimlerback.utils.InvitationEvent;
import com.tim1.daimlerback.utils.VerificationToken;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PassengerService {
    @Autowired
    private IPassengerRepository passengerRepository;

    @Autowired
    private ITokenRepository tokenRepository;

    @Autowired
    private IRideRepository rideRepository;

    @Autowired
    private IFavoriteRouteRepository routeRepository;

    @Autowired
    private ILocationRepository locationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    public PassengersDTO getAllPassengers(Integer page, Integer size) {
        List<Passenger> passengers = passengerRepository.findAll();
        if(passengers.isEmpty()) throw new  ResponseStatusException(HttpStatus.NOT_FOUND, "No passengers found!");
        PassengersDTO dto = new PassengersDTO();
        dto.setTotalCount(passengers.size());
        ArrayList<PassengerDTO> passengerDTOS = new ArrayList<PassengerDTO>();
        for (Passenger p : passengers)
            passengerDTOS.add(new PassengerDTO(p));
        dto.setResults(passengerDTOS);
        return dto;
    }

    public Passenger getPassenger(int id) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        if(passenger.isEmpty()) throw new  ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        else return passenger.get();
    }

    public Passenger getPassenger(String email) {
        Optional<Passenger> passenger = passengerRepository.findByEmail(email);
        if(passenger.isEmpty()) throw new  ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        else return passenger.get();
    }

    public Passenger register(RegisterDTO registerDTO)  {
        Optional<Passenger> passengerCheck = passengerRepository.findByEmail(registerDTO.getEmail());
        if (!passengerCheck.isEmpty()) {
            String value = "Account with that email already exists";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }

        if (registerDTO.getAddress() == null || registerDTO.getEmail() == null || registerDTO.getName() == null
        || registerDTO.getSurname() == null || registerDTO.getProfilePicture() == null || registerDTO.getTelephoneNumber() == null) {
            String value = "Bad input data";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }

        Passenger passenger = new Passenger();

        passenger.setEnabled(false);
        passenger.setBlocked(false);
        passenger.setName(registerDTO.getName());
        passenger.setSurname(registerDTO.getSurname());
        passenger.setProfilePicture(registerDTO.getProfilePicture());
        passenger.setTelephoneNumber(registerDTO.getTelephoneNumber());
        passenger.setEmail(registerDTO.getEmail());
        passenger.setAddress(registerDTO.getAddress());
        passenger.setRole(ERole.ROLE_PASSENGER);
        passenger.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        System.out.println(passenger.getEnabled());

        return save(passenger);
    }

    public void createVerificationToken(Passenger passenger, String token) {
        VerificationToken myToken = new VerificationToken(passenger, token);
        tokenRepository.save(myToken);
    }

    public void verify(String token) {
       Optional<VerificationToken> tokenCheck = tokenRepository.findByToken(token);
       if (tokenCheck.isEmpty()) {
           String value = "message: Verification token not found";
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
       }
       VerificationToken verificationToken = tokenCheck.get();
       LocalDate now = LocalDate.now();
       if (verificationToken.getExpiryDate().isBefore(now)) {
           String value = "message: Token expired";
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
       }
       Passenger passenger = verificationToken.getPassenger();
       passenger.setEnabled(true);
       System.out.println(passenger.getEnabled());
       passengerRepository.save(passenger);
    }

    public Passenger save(Passenger passenger) {
        return passengerRepository.save(passenger);
    }

    public Passenger update(Integer id, UpdateUserDTO passengerDTO) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        if(passenger.isEmpty()) {
            String value = "poruka: Invalid User";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }

        Passenger updated = passenger.get();
        updated.setName(passengerDTO.getName());
        updated.setSurname(passengerDTO.getSurname());
        updated.setAddress(passengerDTO.getAddress());
        updated.setEmail(passengerDTO.getEmail());
        updated.setProfilePicture(passengerDTO.getProfilePicture());
        updated.setTelephoneNumber(passengerDTO.getTelephoneNumber());
        if(!passengerDTO.getPassword().isEmpty()) updated.setPassword(passengerDTO.getPassword());

        return save(updated);
    }

    public Page<Ride> getRides(Integer id, Integer page, Integer elementsPerPage, String sortBy) {
        PageRequest query = PageRequest.of(page, elementsPerPage, Sort.by(sortBy));
        Page<Ride> rides = rideRepository.findRideByPassengerId(id, query);
        return rides;
    }


    public FavoriteRoute save(FavoriteRoute route) {return routeRepository.save(route);}


    public void handleInvitationResponse(InvitationResponseDTO dto) {
        template.convertAndSend("queue/passenger/" + dto.getInviterId(),
                "invite," + dto.getInvitedEmail() + "," + dto.getAccepted() + ","+ dto.getInvitedId());
    }

    public void invite(InvitationDTO dto) {
        Optional<Passenger> invited = passengerRepository.findByEmail(dto.getInvitedEmail());
        if (invited.isEmpty()) {
            String value = "Passenger not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        Passenger p = invited.get();
        Ride activeRide = getPassengerActiveRide(p.getId());
        if (activeRide != null) {
            String value = "Passenger is already on a ride!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        eventPublisher.publishEvent(new InvitationEvent(dto));
    }

    public Ride getPassengerActiveRide(Integer passengerId) {
        List<Ride> pendingRides = rideRepository.findPendingRideByPassengerId(passengerId);
        if (pendingRides.size() == 0) return null;
        return pendingRides.get(0);
    }
}
