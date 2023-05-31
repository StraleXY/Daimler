package selenium.pages;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InboxPage extends UserPageBase {
    @FindBy(xpath = "//app-chat//p[text()=\"We are sorry for inconvenience. How can we help?\"]") private WebElement panicMessage;
    public InboxPage(WebDriver driver) {
        super(driver);
    }

    public boolean isPanicMessageVisible() {
        try {
            interactor.waitForVisibility(panicMessage);
            return true;
        } catch(TimeoutException exception) {
            return false;
        }
    }
}
