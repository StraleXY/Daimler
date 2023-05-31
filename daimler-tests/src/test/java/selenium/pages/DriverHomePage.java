package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import selenium.config.Consts;

public class DriverHomePage extends UserPageBase {

    public DriverHomePage(WebDriver driver) {
        super(driver);
    }
    public void logout() {
        sidebar.goTo(SidebarPage.SidebarRoutes.LOGOUT);
    }

    @FindBy(tagName = "app-driver-new-ride") private WebElement newRideCard;
    @FindBy(xpath = ".//div[contains(@class,'buttons')]/button/span[contains(text(),'START')]/..") private WebElement startRideButton;
    @FindBy(xpath = ".//div[contains(@class,'buttons')]/button/span[contains(text(),'END')]/..") private WebElement endRideButton;
    @FindBy(xpath = ".//div[contains(@class,'buttons')]/button/span[contains(text(),'REJECT')]/..") private WebElement rejectRideButton;
    @FindBy(xpath = ".//div[contains(@class,'buttons')]/button/span[contains(text(),'PANIC')]/..") private WebElement panicRideButton;
    @FindBy(name = "rejectionReason") private WebElement rejectInput;
    @FindBy(xpath = ".//app-driver-reject//button/span[contains(text(),'REJECT')]/..") private WebElement rejectButton;
    @FindBy(xpath = ".//app-driver-new-ride//p[contains(@class,'email')]") private WebElement passengerEmailParagraph;
    @FindBy(name = "status") private WebElement statusButtons;

    public boolean hasRide() {
        try {
            interactor.waitForVisibility(newRideCard);
            return true;
        } catch(TimeoutException exception) {
            return false;
        }
    }

    public boolean isRideStarted() {
        try {
            interactor.waitForVisibility(endRideButton);
            return true;
        } catch(TimeoutException exception) {
            return false;
        }
    }

    public String getRidePassengerEmail() {
        try {
            interactor.waitForVisibility(passengerEmailParagraph);
            return passengerEmailParagraph.getText().trim();
        } catch(TimeoutException exception) {
            return "";
        }
    }

    public boolean isRideFinished() {
        return interactor.awaitAlert();
    }

    public void startRide() {
        interactor.waitAndClick(startRideButton);
    }

    public void endRide() {
        interactor.waitAndClick(endRideButton);
    }

    public void rejectRide(String reason) {
        interactor.waitAndClick(rejectRideButton);
        interactor.waitAndClick(rejectInput);
        rejectInput.sendKeys(reason);
        interactor.waitAndClick(rejectButton);
    }

    public boolean tryDismissAlert() {
        return interactor.awaitAlert();
    }

    public boolean isStatusVisible() {
        try {
            interactor.waitForVisibility(statusButtons);
            return true;
        } catch(TimeoutException exception) {
            return false;
        }
    }

    public void panicRide() {
        interactor.waitAndClick(panicRideButton);
    }
}
