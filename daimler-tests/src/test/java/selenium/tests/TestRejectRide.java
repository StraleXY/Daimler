package selenium.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import selenium.config.Consts;
import selenium.pages.DriverHomePage;
import selenium.pages.LoginPage;
import selenium.pages.PassengerHomePage;

public class TestRejectRide extends TestBase{
    public void loginAndCreateRide(LoginPage loginPage) {
        loginPage.loginWithCredentials(Consts.PASSENGER_MAIN_EMAIL, Consts.PASSENGER_MAIN_PASSWORD);
        PassengerHomePage passengerHome = new PassengerHomePage(driver);
        passengerHome.getRide(Consts.PICKUP_ADDRESS, Consts.DESTINATION_ADDRESS, PassengerHomePage.CarTypes.COUPE);
        Assumptions.assumeTrue(passengerHome.isRouteCalculated());
        passengerHome.confirmRide();
        passengerHome.soloRide();
        passengerHome.setMoreOptions(Consts.IS_BABY, Consts.IS_PET, Consts.IS_FAVORITE);
        Assumptions.assumeTrue(passengerHome.isDriverComing());
        passengerHome.logout();
    }

    @Test
    public void rejectRide() {
        LoginPage loginPage = new LoginPage(driver);
        loginAndCreateRide(loginPage);
        loginPage.loginWithCredentials(Consts.DRIVER_MAIN_EMAIL, Consts.DRIVER_MAIN_PASSWORD);

        DriverHomePage driverHome = new DriverHomePage(driver);
        Assumptions.assumeTrue(driverHome.hasRide());

        driverHome.rejectRide(Consts.REJECT_MESSAGE);
        boolean isDismissed = driverHome.tryDismissAlert();
        Assertions.assertTrue(isDismissed);

        Assumptions.assumeTrue(driverHome.isStatusVisible());
    }
}
