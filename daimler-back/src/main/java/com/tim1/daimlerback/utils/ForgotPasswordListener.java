package com.tim1.daimlerback.utils;

import com.tim1.daimlerback.entities.Passenger;
import com.tim1.daimlerback.entities.User;
import com.tim1.daimlerback.services.PassengerService;
import com.tim1.daimlerback.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ForgotPasswordListener implements ApplicationListener<ForgotPasswordEvent> {
    @Autowired
    private UserService service;

    @Autowired
    private SendGridMailService mailService;

    @Override
    public void onApplicationEvent(ForgotPasswordEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createRecoveryToken(user, token);
        mailService.sendPasswordRecoveryMail(token, user.getEmail());
    }

}
