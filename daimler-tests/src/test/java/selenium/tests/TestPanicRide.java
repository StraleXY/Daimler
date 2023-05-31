package selenium.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import selenium.config.Consts;
import selenium.pages.DriverHomePage;
import selenium.pages.InboxPage;
import selenium.pages.LoginPage;
import selenium.pages.PassengerHomePage;

public class TestPanicRide extends TestBase {
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
    public void panicRideDriver() {
        LoginPage loginPage = new LoginPage(driver);
        loginAndCreateRide(loginPage);
        loginPage.loginWithCredentials(Consts.DRIVER_MAIN_EMAIL, Consts.DRIVER_MAIN_PASSWORD);

        DriverHomePage driverHome = new DriverHomePage(driver);
        Assumptions.assumeTrue(driverHome.hasRide());
        driverHome.startRide();

        driverHome.panicRide();
        boolean isDismissed = driverHome.tryDismissAlert();
        Assertions.assertTrue(isDismissed);

        InboxPage inboxPage = new InboxPage(driver);
        Assertions.assertEquals(driver.getCurrentUrl(), Consts.DRIVER_INBOX_PAGE_URL);
        Assumptions.assumeTrue(inboxPage.isPanicMessageVisible());
        driverHome.logout();
    }

    @Test
    public void panicRidePassenger() {
        LoginPage loginPage = new LoginPage(driver);
        loginAndCreateRide(loginPage);
        loginPage.loginWithCredentials(Consts.DRIVER_MAIN_EMAIL, Consts.DRIVER_MAIN_PASSWORD);

        DriverHomePage driverHome = new DriverHomePage(driver);
        Assumptions.assumeTrue(driverHome.hasRide());
        driverHome.startRide();
        driverHome.logout();

        loginPage.loginWithCredentials(Consts.PASSENGER_MAIN_EMAIL, Consts.PASSENGER_MAIN_PASSWORD);
        PassengerHomePage passengerHome = new PassengerHomePage(driver);

        passengerHome.panicRide();
        boolean isDismissed = passengerHome.tryDismissAlert();
        Assertions.assertTrue(isDismissed);

        InboxPage inboxPage = new InboxPage(driver);
        Assertions.assertEquals(driver.getCurrentUrl(), Consts.PASSENGER_INBOX_PAGE_URL);
        Assumptions.assumeTrue(inboxPage.isPanicMessageVisible());
        passengerHome.logout();
    }
}
