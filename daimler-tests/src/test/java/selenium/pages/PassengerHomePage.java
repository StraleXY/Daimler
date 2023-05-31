package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PassengerHomePage extends UserPageBase {

    public PassengerHomePage(WebDriver driver) {
        super(driver);
    }
    public void logout() {
        sidebar.goTo(SidebarPage.SidebarRoutes.LOGOUT);
    }

    /* ------------------------------------------------- GET A RIDE ------------------------------------------------- */
    private static final String CAR_TYPE_SELECTOR = ".//span[contains(text(), 'TYPE')]/..";
    private static final String CAR_TYPE_KEY = "TYPE";
    public enum CarTypes {
        LIMOUSINE("Limousine"),
        COUPE("Coupe"),
        SUV("SUV");
        public final String key;
        CarTypes(String key) {
            this.key = key;
        }
    }
    @FindBy(css = "mat-select[ng-reflect-name='carType']") private WebElement carTypeSelect;
    @FindBy(xpath = ".//span[contains(text(), 'Coupe')]/..") private WebElement typeCoupeOption;
    @FindBy(name = "pickupAddress") private WebElement pickupAddressInput;
    @FindBy(name = "destinationAddress") private WebElement destinationAddressInput;
    @FindBy(id = "get-a-ride-calculate") private WebElement calculateButton;

    public void getRide(String pickup, String destination, CarTypes carType) {
        interactor.waitAndClick(carTypeSelect);
        interactor.waitAndClick(driver.findElement(By.xpath(CAR_TYPE_SELECTOR.replace(CAR_TYPE_KEY, carType.key))));
        interactor.waitAndClear(pickupAddressInput);
        pickupAddressInput.sendKeys(pickup);
        interactor.waitAndClear(destinationAddressInput);
        destinationAddressInput.sendKeys(destination);
        interactor.waitAndClick(calculateButton);
    }


    /* ------------------------------------------------ CONFIRM RIDE ------------------------------------------------ */
    @FindBy(className = "confirm-ride") private WebElement confirmRideButton;
    @FindBy(css = "app-route-compact") private WebElement routeInfoCard;
    public boolean isRouteCalculated() {
        try {
            interactor.waitForVisibility(routeInfoCard);
            return true;
        } catch (TimeoutException timeout) {
            return false;
        }
    }
    public void confirmRide() {
        interactor.waitAndClick(confirmRideButton);
    }

    /* ------------------------------------------------- SOLO RIDE -------------------------------------------------- */
    @FindBy(id = "passenger-count-solo") private WebElement soloButton;

    public void soloRide() {
        interactor.waitAndClick(soloButton);
    }

    /* ----------------------------------------------- MORE SETTINGS ------------------------------------------------ */
    @FindBy(xpath = "(.//app-more-ride-settings//input[contains(@class, 'time-picker-input')])[1]") private WebElement scheduleHoursPicker;
    @FindBy(xpath = "(.//app-more-ride-settings//input[contains(@class, 'time-picker-input')])[2]") private WebElement scheduleMinutesPicker;
    @FindBy(xpath = ".//app-more-ride-settings//label[text() = 'Baby']/../..") private WebElement babyCheckbox;
    @FindBy(xpath = ".//app-more-ride-settings//label[text() = 'Pets']/../..") private WebElement petsCheckbox;
    @FindBy(xpath = ".//app-more-ride-settings//label[text() = 'Add to favourites']/../..") private WebElement favoritesCheckbox;
    @FindBy(xpath = ".//button/span[contains(text(),'PANIC')]/..") private WebElement panicRideButton;
    @FindBy(xpath = ".//app-scheduled-ride") private WebElement scheduledCard;
    @FindBy(id = "more-settings-confirm") private WebElement confirmMoreOptions;

    public void setMoreOptions(boolean isBaby, boolean isPets, boolean isFavorite) {
        if(isBaby) interactor.waitAndClick(babyCheckbox);
        if(isPets) interactor.waitAndClick(petsCheckbox);
        if(isFavorite) interactor.waitAndClick(favoritesCheckbox);
        interactor.waitAndClick(confirmMoreOptions);
    }

    public void setMoreOptions(boolean isBaby, boolean isPets, boolean isFavorite, int hours, int minutes) {
        interactor.waitAndClear(scheduleHoursPicker);
        scheduleHoursPicker.sendKeys(String.valueOf(hours));
        interactor.waitAndClear(scheduleMinutesPicker);
        scheduleMinutesPicker.sendKeys(String.valueOf(minutes));
        setMoreOptions(isBaby, isPets, isFavorite);
    }

    /* ----------------------------------------------- DRIVER COMING ------------------------------------------------ */
    @FindBy(css = "app-driver-coming #on-the-way") private WebElement driverComingText;

    public boolean isDriverComing() {
        try {
            interactor.waitForVisibility(driverComingText);
            return true;
        } catch (TimeoutException timeout) {
            return false;
        }
    }

    /* ------------------------------------------------- NO COMING -------------------------------------------------- */

    public boolean isNoDriverAlert() {
        return interactor.awaitAlert();
    }

    public boolean tryDismissAlert() {
        return interactor.awaitAlert();
    }

    public void panicRide() {
        interactor.waitAndClick(panicRideButton);
    }

    public boolean isScheduledVisible() {
        try {
            interactor.waitForVisibility(scheduledCard);
            return true;
        } catch(TimeoutException exception) {
            return false;
        }
    }
}
