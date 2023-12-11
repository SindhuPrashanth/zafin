package reusable;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class JavaReusableFunctions {
    public static String getChromeDriverPath(){
        String chromeDriverPath = System.getProperty("user.dir")+"/src/main/resources/drivers/chromedriver";
        return chromeDriverPath;
    }

    public String getGlobalValue(String key) throws IOException {
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"/src/main/resources/global.properties");
        prop.load(fis);
        return prop.getProperty(key);

    }

}


