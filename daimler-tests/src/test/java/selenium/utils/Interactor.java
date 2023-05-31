package selenium.utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Interactor {
    private WebDriver driver;
    public Interactor(WebDriver driver) {
        this.driver = driver;
    }
    public void waitAndClick(WebElement element) {
        (new WebDriverWait(driver, 5)).until(ExpectedConditions.elementToBeClickable(element)).click();
    }
    public void waitAndClear(WebElement element) {
        (new WebDriverWait(driver, 5)).until(ExpectedConditions.visibilityOf(element)).clear();
    }

    public void waitForVisibility(WebElement element) {
        (new WebDriverWait(driver, 5)).until(ExpectedConditions.visibilityOf(element));
    }

    public boolean awaitAlert() {
        try {
            Alert alert = (new WebDriverWait(driver, 3)).until(ExpectedConditions.alertIsPresent());
            alert.dismiss();
            return true;
        } catch (TimeoutException timeout) {
            return false;
        }
    }
}
