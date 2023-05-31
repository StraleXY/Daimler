package selenium.tests;

import com.sun.jdi.ThreadReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import selenium.config.Consts;
import selenium.pages.DriverHomePage;
import selenium.pages.LoginPage;
import selenium.pages.PassengerAccountPage;
import selenium.pages.PassengerHomePage;

import java.time.LocalDateTime;

public class TestCreateRide extends TestBase {

    @Test
    public void createRide() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginWithCredentials(Consts.PASSENGER_MAIN_EMAIL, Consts.PASSENGER_MAIN_PASSWORD);

        PassengerHomePage passengerHome = new PassengerHomePage(driver);

        passengerHome.getRide(Consts.PICKUP_ADDRESS, Consts.DESTINATION_ADDRESS, PassengerHomePage.CarTypes.COUPE);
        Assertions.assertTrue(passengerHome.isRouteCalculated());

        passengerHome.confirmRide();
        passengerHome.soloRide();
        passengerHome.setMoreOptions(Consts.IS_BABY, Consts.IS_PET, Consts.IS_FAVORITE);

        Assertions.assertTrue(passengerHome.isDriverComing());

        PassengerAccountPage passengerAccount = new PassengerAccountPage(driver);
        passengerAccount.navigateTo();
        Assertions.assertEquals(Consts.PICKUP_ADDRESS, passengerAccount.getFavoriteRouteFrom());
        Assertions.assertEquals(Consts.DESTINATION_ADDRESS, passengerAccount.getFavoriteRouteTo());

        passengerHome.logout();
        loginPage.loginWithCredentials(Consts.DRIVER_MAIN_EMAIL, Consts.DRIVER_MAIN_PASSWORD);

        DriverHomePage driverHome = new DriverHomePage(driver);
        Assertions.assertTrue(driverHome.hasRide());
        Assertions.assertEquals(Consts.PASSENGER_MAIN_EMAIL, driverHome.getRidePassengerEmail());
    }

    @Test
    public void noDriverFound() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginWithCredentials(Consts.PASSENGER_REJECTED_EMAIL, Consts.PASSENGER_REJECTED_PASSWORD);

        PassengerHomePage homePage = new PassengerHomePage(driver);

        homePage.getRide(Consts.PICKUP_ADDRESS, Consts.DESTINATION_ADDRESS, PassengerHomePage.CarTypes.LIMOUSINE);
        Assertions.assertTrue(homePage.isRouteCalculated());

        homePage.confirmRide();
        homePage.soloRide();
        homePage.setMoreOptions(true, false, false);

        Assertions.assertTrue(homePage.isNoDriverAlert());
    }

    @Test
    public void scheduleRide() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginWithCredentials(Consts.PASSENGER_MAIN_EMAIL, Consts.PASSENGER_MAIN_PASSWORD);

        PassengerHomePage passengerHome = new PassengerHomePage(driver);

        passengerHome.getRide(Consts.PICKUP_ADDRESS, Consts.DESTINATION_ADDRESS, PassengerHomePage.CarTypes.COUPE);
        Assertions.assertTrue(passengerHome.isRouteCalculated());

        passengerHome.confirmRide();
        passengerHome.soloRide();

        LocalDateTime now = LocalDateTime.now();
        int hours = now.getHour() + 1;
        int minutes = now.getMinute();

        passengerHome.setMoreOptions(Consts.IS_BABY, Consts.IS_PET, Consts.IS_FAVORITE, hours, minutes);
        Assumptions.assumeTrue(passengerHome.isScheduledVisible());
    }
}
