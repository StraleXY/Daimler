package selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SidebarPage extends PageBase {

    public static enum SidebarRoutes {
        HOME,
        ACCOUNT,
        HISTORY,
        LOGOUT
    }

    public SidebarPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "sidebar_home")
    WebElement sidebarHomeButton;

    @FindBy(id = "sidebar_account")
    WebElement sidebarAccountButton;

    @FindBy(id = "sidebar_history")
    WebElement sidebarHistoryButton;
    @FindBy(id = "sidebar_logout")
    WebElement sidebarLogoutButton;

    public void goTo(SidebarRoutes route) {
        switch (route){
            case HOME -> interactor.waitAndClick(sidebarHomeButton);
            case ACCOUNT -> interactor.waitAndClick(sidebarAccountButton);
            case HISTORY -> interactor.waitAndClick(sidebarHistoryButton);
            case LOGOUT -> interactor.waitAndClick(sidebarLogoutButton);
        }
    }
}
