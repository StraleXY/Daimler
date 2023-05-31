package com.tim1.daimlerback.controllers;

import com.tim1.daimlerback.dtos.common.PasswordDTO;
import com.tim1.daimlerback.dtos.common.RidesDTO;
import com.tim1.daimlerback.dtos.driver.GraphStatsDTO;
import com.tim1.daimlerback.dtos.user.*;
import com.tim1.daimlerback.entities.User;
import com.tim1.daimlerback.entities.enumeration.ERole;
import com.tim1.daimlerback.services.MessagesService;
import com.tim1.daimlerback.services.RideService;
import com.tim1.daimlerback.services.UserService;
import com.tim1.daimlerback.utils.ForgotPasswordEvent;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private MessagesService messagesService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private RideService rideService;

    @GetMapping(value="{id}/ride")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RidesDTO> getRides(@PathVariable Integer id,
                                             @RequestParam Integer page,
                                             @RequestParam Integer size,
                                             @AuthenticationPrincipal User user) {
        if (user.getRole() != ERole.ROLE_ADMIN && !id.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        return new ResponseEntity<>(rideService.getRidesForUser(id, page, size), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UsersDTO> getUsers(@RequestParam Integer page,
                                             @RequestParam Integer size) {
        UsersDTO dto = new UsersDTO(userService.findAll(page, size));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("{id}/message")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessagesDTO> getMessages(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        if (!id.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        MessagesDTO dto = messagesService.getUserMessages(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("message/{from_id}/{to_id}/{type}/{rideId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessagesDTO> getMessages(@PathVariable Integer from_id, @PathVariable Integer to_id, @PathVariable String type, @PathVariable Integer rideId, @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(messagesService.getAllBetween(from_id, to_id, type, rideId), HttpStatus.OK);
    }

    @GetMapping("inbox/{id}")
    public ResponseEntity<List<InboxDTO>> getInbox(@PathVariable Integer id) {
        return new ResponseEntity<>(messagesService.getUserInbox(id), HttpStatus.OK);
    }

    @PostMapping(value = "{from_id}/message", consumes = "application/json")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageDTO> sendMessage(@PathVariable Integer from_id, @RequestBody CreateMessageDTO createDto, @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(messagesService.sendMessage(from_id, createDto), HttpStatus.OK);
    }

    @PutMapping("{id}/block")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> block(@PathVariable Integer id) {
        if (userService.block(id)) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("{id}/unblock")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> unblock(@PathVariable Integer id) {
        if (userService.unblock(id)) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "{id}/note", consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<NoteDTO> createNote(@PathVariable Integer id,
                                              @RequestBody NoteShortDTO dto) {
        return new ResponseEntity<>(new NoteDTO(userService.insertNote(id, dto)), HttpStatus.OK);
    }

    @GetMapping(value = "{id}/note")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<NotesDTO> getNotes(@PathVariable Integer id,
                                             @RequestParam Integer page,
                                             @RequestParam Integer size,
                                             @RequestParam String sort) {
        NotesDTO dto = new NotesDTO(userService.getNotes(id, page, size, sort).stream().map(NoteDTO::new).collect(Collectors.toList()));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(value = "login", consumes = "application/json")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDto) {
        try {
            return ResponseEntity.ok(userService.login(loginDto));
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        User user = userService.findByEmail(email);
        eventPublisher.publishEvent(new ForgotPasswordEvent(user));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "recover/{token}")
    public ResponseEntity<?> recover(@PathVariable String token, @RequestParam String newPassword) {
        userService.changePassword(token, newPassword);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PASSENGER') or hasRole('ROLE_DRIVER')")
    @PutMapping(consumes = "application/json", value = "{id}/changePassword")
    public ResponseEntity<?> changePassword(@PathVariable Integer id, @RequestBody PasswordDTO dto) {
        userService.changePassword(id, dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "stats/{id}/{from}/{to}")
    public ResponseEntity<UserGraphStatsDTO> getStats(@PathVariable Integer id, @PathVariable Long from, @PathVariable Long to) {
        return new ResponseEntity<>(userService.getStats(id, from, to), HttpStatus.OK);
    }
}
