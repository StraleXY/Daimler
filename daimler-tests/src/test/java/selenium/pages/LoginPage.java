package selenium.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage extends PageBase {
    private static final String PAGE_URL="http://localhost:4200/login";

    public LoginPage(WebDriver driver){
        super(driver, PAGE_URL);
    }

    @FindBy(name="email")
    private WebElement emailField;

    @FindBy(name="password")
    private WebElement passwordField;

    @FindBy(id = "button_login")
    private WebElement loginButton;

    public void loginWithCredentials(String email, String password) {
        interactor.waitAndClear(emailField);
        emailField.sendKeys(email);
        interactor.waitAndClear(passwordField);
        passwordField.sendKeys(password);
        interactor.waitAndClick(loginButton);
    }

    public boolean tryDismissAlert() {
        return interactor.awaitAlert();
    }
}
