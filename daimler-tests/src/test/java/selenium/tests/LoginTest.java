package selenium.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import selenium.config.Consts;
import selenium.pages.LoginPage;
import selenium.pages.PassengerAccountPage;

public class LoginTest extends TestBase {

    private static final String WRONG_EMAIL = "wrong@email.com";
    private static final String WRONG_PASSWORD = "wrong_password";

    @Test
    public void noCredentials() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginWithCredentials("", "");
        boolean isError = loginPage.tryDismissAlert();
        Assertions.assertTrue(isError);
    }

    @Test
    public void wrongPasswordCredentials() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginWithCredentials(Consts.PASSENGER_MAIN_EMAIL, WRONG_PASSWORD);
        boolean isError = loginPage.tryDismissAlert();
        Assertions.assertTrue(isError);
    }

    @Test
    public void correctCredentials() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginWithCredentials(Consts.PASSENGER_MAIN_EMAIL, Consts.PASSENGER_MAIN_PASSWORD);
        boolean isError = loginPage.tryDismissAlert();
        Assertions.assertFalse(isError);

        PassengerAccountPage accountPage = new PassengerAccountPage(driver);
        Assertions.assertEquals(Consts.PASSENGER_MAIN_EMAIL, accountPage.getPassengerEmail());
    }
}
