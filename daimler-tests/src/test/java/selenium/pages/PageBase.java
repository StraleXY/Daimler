package selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.utils.Interactor;

public abstract class PageBase {

    protected WebDriver driver;
    protected Interactor interactor;

    public PageBase(WebDriver driver) {
        this.driver = driver;
        this.interactor = new Interactor(driver);
        PageFactory.initElements(driver, this);
    }

    public PageBase(WebDriver driver, String url) {
        this.driver = driver;
        this.interactor = new Interactor(driver);
        driver.get(url);
        PageFactory.initElements(driver, this);
    }
}
