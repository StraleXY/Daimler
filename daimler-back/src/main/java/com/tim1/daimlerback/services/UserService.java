package com.tim1.daimlerback.services;
import com.tim1.daimlerback.dtos.common.PasswordDTO;
import com.tim1.daimlerback.dtos.driver.GraphStatsDTO;
import com.tim1.daimlerback.dtos.user.*;
import com.tim1.daimlerback.entities.*;
import com.tim1.daimlerback.entities.enumeration.ERole;
import com.tim1.daimlerback.repositories.*;
import com.tim1.daimlerback.security.JwtUtils;
import com.tim1.daimlerback.utils.RecoveryToken;
import com.tim1.daimlerback.utils.VerificationToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private IRecoveryTokenRepository tokenRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IDriverRepository driverRepository;
    @Autowired
    private IPassengerRepository passengerRepository;
    @Autowired
    private INotesRepository notesRepository;
    @Autowired
    private IRideRepository rideRepository;

    public TokenDTO login(LoginDTO loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = (User) authentication.getPrincipal();

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setAccessToken(jwt);
        tokenDTO.setRefreshToken(""); // Not used at the moment
        tokenDTO.setUserId(user.getId());
        tokenDTO.setUserRole(user.getRole());

        return tokenDTO;
    }

    public List<UserDTO> findAll(Integer page, Integer elementsPerPage) {
        PageRequest query = PageRequest.of(page, elementsPerPage);
        Page<User> users = userRepository.findAllByRoleIn(new ArrayList<>(List.of(new ERole[]{ERole.ROLE_PASSENGER, ERole.ROLE_DRIVER})), query);
        List<UserDTO> usersDTOs = new ArrayList<>();
        for (User user : users.stream().toList()) {
            usersDTOs.add(user.getRole() == ERole.ROLE_DRIVER ? new UserDTO(driverRepository.findById(user.getId()).get()) : new UserDTO(passengerRepository.findById(user.getId()).get()));
        }
        return usersDTOs;
    }

    public boolean block(Integer userId) {
        return blocking(userId, true, "message: User already blocked!");
    }

    public boolean unblock(Integer userId) {
       return blocking(userId, false, "message: User is not blocked!");
    }

    private boolean blocking(Integer id, boolean block, String errorMsg) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist!");
        if (userOptional.get().getRole() == ERole.ROLE_DRIVER) {
            Optional<Driver> driverOptional = driverRepository.findById(id);
            if(driverOptional.get().getBlocked() == block) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg);
            driverOptional.get().setBlocked(block);
            driverRepository.save(driverOptional.get());
            return true;
        }
        else if (userOptional.get().getRole() == ERole.ROLE_PASSENGER) {
            Optional<Passenger> passengerOptional = passengerRepository.findById(id);
            if(passengerOptional.get().getBlocked() == block) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg);
            passengerOptional.get().setBlocked(block);
            passengerRepository.save(passengerOptional.get());
            return true;
        }
        return false;
    }

    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            String value = "message: User with specified email not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        return user.get();
    }

    public void createRecoveryToken(User user, String token) {
        RecoveryToken myToken = new RecoveryToken(user, token);
        tokenRepository.save(myToken);
    }

    public void changePassword(Integer id, PasswordDTO dto) {
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 8) {
            String value = "Bad data input";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            String value = "message: User not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        User u = user.get();
        if (!u.getPassword().equals(dto.getOldPassword())) {
            String value = "message: Current password is not matching!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        u.setPassword(dto.getNewPassword());
        userRepository.save(u);
    }

    public void changePassword(String token, String newPassword) {
        Optional<RecoveryToken> recoveryToken = tokenRepository.findByToken(token);
        if (recoveryToken.isEmpty()) {
            String value = "message: Invalid recovery token";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        Integer userId = recoveryToken.get().getUser().getId();
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            String value = "message: Invalid recovery token";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        user.get().setPassword(newPassword);
        userRepository.save(user.get());
    }

    public List<Note> getNotes(Integer userId, Integer page, Integer elementsPerPage, String sortBy) {
        PageRequest query = PageRequest.of(page, elementsPerPage, Sort.by(sortBy));
        Page<Note> notePage = notesRepository.findAllByUserId(userId, query);
        if (notePage.isEmpty()) {
            String value = "User does not exist!";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        return notePage.stream().toList();
    }

    public Note insertNote(Integer userId, NoteShortDTO shortDTO) {
        if (shortDTO.getMessage() == null){
            String value = "Bad input data";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        if (shortDTO.getMessage().length() > 255) {
            String value = "Note too long!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist!");
        Note note = new Note(shortDTO.getMessage(), userId);
        return notesRepository.save(note);
    }

    public UserGraphStatsDTO getStats(Integer id, Long from, Long to) {
        Optional<User> requestedFor = userRepository.findById(id);
        if (requestedFor.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist!");
        UserGraphStatsDTO stats = new UserGraphStatsDTO();
        Calendar startInterval = Calendar.getInstance();
        startInterval.setTimeInMillis(from);
        startInterval.add(Calendar.HOUR_OF_DAY, startInterval.get(Calendar.HOUR_OF_DAY));
        startInterval.add(Calendar.MINUTE, startInterval.get(Calendar.MINUTE));
        Calendar toInterval = Calendar.getInstance();
        toInterval.setTimeInMillis(startInterval.getTimeInMillis());
        toInterval.add(Calendar.DAY_OF_MONTH, 1);
        Calendar endInterval = Calendar.getInstance();
        endInterval.setTimeInMillis(to);
        endInterval.add(Calendar.DAY_OF_MONTH, 1);
        while (toInterval.getTimeInMillis() < endInterval.getTimeInMillis()) {
            List<Ride> finished = null;
            if (requestedFor.get().getRole() == ERole.ROLE_DRIVER) finished = rideRepository.findFinishedByDriverId(id, startInterval.getTimeInMillis(), toInterval.getTimeInMillis());
            else finished = rideRepository.findFinishedByPassengerId(id, startInterval.getTimeInMillis(), toInterval.getTimeInMillis());
            stats.addRideDay(finished.size());
            if (finished.size() > 0) {
                stats.addDistanceDay((int) finished.stream().mapToDouble(Ride::getDistance).sum());
                stats.addAmountDay(finished.stream().mapToInt(Ride::getTotalCost).sum());
            } else {
                stats.addDistanceDay(0);
                stats.addAmountDay(0);
            }
            startInterval.add(Calendar.DAY_OF_MONTH, 1);
            toInterval.add(Calendar.DAY_OF_MONTH, 1);
        }
        return stats;
    }
}
