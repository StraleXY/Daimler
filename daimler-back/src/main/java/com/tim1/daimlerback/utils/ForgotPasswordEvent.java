package com.tim1.daimlerback.utils;

import com.tim1.daimlerback.entities.Passenger;
import com.tim1.daimlerback.entities.User;
import org.springframework.context.ApplicationEvent;

public class ForgotPasswordEvent extends ApplicationEvent {
    private User user;

    public ForgotPasswordEvent(User user) {
        super(user);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
