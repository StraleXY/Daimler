package com.tim1.daimlerback.utils;

import com.tim1.daimlerback.entities.Passenger;
import com.tim1.daimlerback.services.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private PassengerService service;

    @Autowired
    private SendGridMailService mailService;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        Passenger passenger = event.getPassenger();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(passenger, token);
        mailService.sendRegistrationMail(token, passenger.getEmail());
    }
}