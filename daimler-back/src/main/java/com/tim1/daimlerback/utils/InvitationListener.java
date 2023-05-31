package com.tim1.daimlerback.utils;

import com.tim1.daimlerback.dtos.ride.InvitationDTO;
import com.tim1.daimlerback.entities.Passenger;
import com.tim1.daimlerback.services.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class InvitationListener implements ApplicationListener<InvitationEvent> {

    @Autowired
    private PassengerService service;

    @Autowired
    private SendGridMailService mailService;

    @Override
    public void onApplicationEvent(InvitationEvent event) {
        this.handleInvitation(event);
    }

    private void handleInvitation(InvitationEvent event) {
        InvitationDTO dto = event.getInvitation();
        Passenger passenger = service.getPassenger(dto.getInviterId());
        Passenger invited = service.getPassenger(dto.getInvitedEmail());
        String name = passenger.getName() + " "  + passenger.getSurname();
        String hrefAccept = "http://localhost:4200/invitation?id=" + dto.getInviterId() + "&email=" + dto.getInvitedEmail() + "&accepted=true&passengerId=" + invited.getId();
        String hrefReject = "http://localhost:4200/invitation?id=" + dto.getInviterId() + "&email=" + dto.getInvitedEmail() + "&accepted=false&passengerId=" + invited.getId();
        mailService.sendRideInvitationMail(dto.getInvitedEmail(), name, dto.getAddressFrom(), dto.getAddressTo(), hrefAccept, hrefReject);
    }
}
