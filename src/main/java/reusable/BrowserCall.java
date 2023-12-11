package reusable;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BrowserCall {
    public static WebDriver createDriver() {
        System.setProperty("webdriver.chrome.drive", JavaReusableFunctions.getChromeDriverPath());
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        WebDriver driver =  new ChromeDriver();
        return driver;
    }
}
