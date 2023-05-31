package com.tim1.daimlerback.utils;

import com.tim1.daimlerback.dtos.ride.InvitationDTO;
import org.springframework.context.ApplicationEvent;

public class InvitationEvent extends ApplicationEvent {
    private InvitationDTO dto;

    public InvitationEvent(InvitationDTO dto) {
        super(dto);
        this.dto = dto;
    }

    public InvitationDTO getInvitation() {
        return this.dto;
    }

    public void setInvitation(InvitationDTO dto) {
        this.dto = dto;
    }
}
