package selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PassengerAccountPage extends UserPageBase {
    public PassengerAccountPage(WebDriver driver) {
        super(driver);
    }

    public void navigateTo() {
        sidebar.goTo(SidebarPage.SidebarRoutes.ACCOUNT);
    }

    @FindBy(xpath = ".//div[@id='user-basic-info']/p[@class='value-item'][2]")
    WebElement userEmail;

    @FindBy(xpath = "(.//app-favorite-routes-card//p[contains(@class,'value-item')])[1]")
    WebElement favoriteRouteFrom;
    @FindBy(xpath = "(.//app-favorite-routes-card//p[contains(@class,'value-item')])[2]")
    WebElement favoriteRouteTo;

    public String getPassengerEmail() {
        navigateTo();
        interactor.waitForVisibility(userEmail);
        return userEmail.getText().trim();
    }

    public String getFavoriteRouteFrom() {
        interactor.waitForVisibility(favoriteRouteFrom);
        return favoriteRouteFrom.getText().trim();
    }

    public String getFavoriteRouteTo() {
        interactor.waitForVisibility(favoriteRouteTo);
        return favoriteRouteTo.getText().trim();
    }

}
