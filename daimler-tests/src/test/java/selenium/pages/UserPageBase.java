package selenium.pages;

import org.openqa.selenium.WebDriver;

public class UserPageBase extends PageBase {

    protected SidebarPage sidebar;

    public UserPageBase(WebDriver driver) {
        super(driver);
        sidebar = new SidebarPage(driver);
    }
}
