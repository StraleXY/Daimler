package selenium.tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class TestBase {

    public static WebDriver driver;

    @BeforeAll
    public static void initWebDriver() {
        //System.setProperty("webdriver.chrome.driver", "chromedriver/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", "chromedriver/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }

    @AfterAll
    public static void quitDriver() {
        driver.quit();
    }
}
