package com.tim1.daimlerback.dtos.ride;

public class InvitationResponseDTO {
    private Integer inviterId;
    private Integer invitedId;
    private String invitedEmail;
    private Boolean accepted;

    public InvitationResponseDTO() {

    }

    public Integer getInviterId() {
        return inviterId;
    }

    public void setInviterId(Integer inviterId) {
        this.inviterId = inviterId;
    }

    public String getInvitedEmail() {
        return invitedEmail;
    }

    public void setInvitedEmail(String invitedEmail) {
        this.invitedEmail = invitedEmail;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Integer getInvitedId() {
        return invitedId;
    }

    public void setInvitedId(Integer invitedId) {
        this.invitedId = invitedId;
    }
}
