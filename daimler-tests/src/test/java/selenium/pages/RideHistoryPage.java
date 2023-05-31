package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RideHistoryPage extends UserPageBase {
    public RideHistoryPage(WebDriver driver) {
        super(driver);
    }

    public void navigateTo() {
        sidebar.goTo(SidebarPage.SidebarRoutes.HISTORY);
    }

    @FindBy(xpath=".//app-route-history-card")
    WebElement rideHistory;

    public boolean hasRide(String from, String to) {
        interactor.waitForVisibility(rideHistory);
        try {
            rideHistory.findElement(By.xpath(".//p[contains(@class,'value-item') and contains(text(),'" + from + "')]/../../div[2]/p[contains(@class,'value-item') and contains(text(),'" + to + "')]"));
            return true;
        } catch(NoSuchElementException exception) {
            return false;
        }
    }

}
