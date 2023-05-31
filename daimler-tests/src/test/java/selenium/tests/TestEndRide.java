package selenium.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import selenium.config.Consts;
import selenium.pages.*;

public class TestEndRide extends TestBase {

    @Test
    public void endRide() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginWithCredentials(Consts.PASSENGER_MAIN_EMAIL, Consts.PASSENGER_MAIN_PASSWORD);

        PassengerHomePage passengerHome = new PassengerHomePage(driver);

        passengerHome.getRide(Consts.PICKUP_ADDRESS, Consts.DESTINATION_ADDRESS, PassengerHomePage.CarTypes.COUPE);
        Assumptions.assumeTrue(passengerHome.isRouteCalculated());

        passengerHome.confirmRide();
        passengerHome.soloRide();
        passengerHome.setMoreOptions(Consts.IS_BABY, Consts.IS_PET, Consts.IS_FAVORITE);

        Assumptions.assumeTrue(passengerHome.isDriverComing());

        passengerHome.logout();
        loginPage.loginWithCredentials(Consts.DRIVER_MAIN_EMAIL, Consts.DRIVER_MAIN_PASSWORD);

        DriverHomePage driverHome = new DriverHomePage(driver);
        Assumptions.assumeTrue(driverHome.hasRide());

        driverHome.startRide();
        Assertions.assertTrue(driverHome.isRideStarted());

        driverHome.endRide();
        Assertions.assertTrue(driverHome.isRideFinished());

        driverHome.logout();
        loginPage.loginWithCredentials(Consts.PASSENGER_MAIN_EMAIL, Consts.PASSENGER_MAIN_PASSWORD);

        RideHistoryPage rideHistory = new RideHistoryPage(driver);
        rideHistory.navigateTo();
        Assertions.assertTrue(rideHistory.hasRide(Consts.PICKUP_ADDRESS, Consts.DESTINATION_ADDRESS));
    }
}
