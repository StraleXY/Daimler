package com.tim1.daimler.util;

import com.tim1.daimler.service.driver.DriverService;
import com.tim1.daimler.service.driver.LoginService;
import com.tim1.daimler.service.driver.MessagesService;
import com.tim1.daimler.service.driver.PassengerService;
import com.tim1.daimler.service.driver.ReviewService;
import com.tim1.daimler.service.driver.RideService;
import com.tim1.daimler.service.driver.UnregisteredUserService;
import com.tim1.daimler.service.driver.UserService;

public class ServiceGenerator {

    public static DriverService driverService;
    public static PassengerService passengerService;
    public static LoginService loginService;
    public static UnregisteredUserService userService;
    public static RideService rideService;
    public static MessagesService messagesService;
    public static UserService registeredUserService;
    public static ReviewService reviewService;

    public static void initDriverService(String authToken) {
        driverService = Servicer.createService(DriverService.class, authToken);
        rideService = Servicer.createService(RideService.class, authToken);
        messagesService = Servicer.createService(MessagesService.class, authToken);
        registeredUserService = Servicer.createService(UserService.class, authToken);
        reviewService = Servicer.createService(ReviewService.class, authToken);
    }

    public static void initLoginService(String authToken) {
        loginService = Servicer.createService(LoginService.class, authToken);
    }

    public static void initPassenger(String authToken) {
        passengerService = Servicer.createService(PassengerService.class, authToken);
        userService = Servicer.createService(UnregisteredUserService.class, authToken);
        rideService = Servicer.createService(RideService.class, authToken);
        messagesService = Servicer.createService(MessagesService.class, authToken);
        registeredUserService = Servicer.createService(UserService.class, authToken);
        reviewService = Servicer.createService(ReviewService.class, authToken);
    }
}
