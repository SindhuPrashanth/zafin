package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import reusable.BrowserCall;
import reusable.PageAccess;

public class SauceHome {

    private static final Logger logger = LogManager.getLogger(SauceHome.class);
    public WebDriver driver = BrowserCall.createDriver();

    @BeforeTest
    public void beforeTest() {
        driver.get("https://www.saucedemo.com/");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test end-2-end happy path flow")
    @Parameters("Credentials")
    public void happyPathTest(String Credentials) {
        WebElement userName = driver.findElement(By.id("user-name"));
        WebElement passWordText = driver.findElement(By.id("password"));
        WebElement logonButton = driver.findElement(By.id("login-button"));
        PageAccess webPage = new PageAccess();
        webPage.sendText(userName, usersFromPage(Credentials));
        webPage.sendText(passWordText, passwordFromPage());
        webPage.clickButton(logonButton);
        logger.debug("url:" + driver.getCurrentUrl());
        Select sortElement = new Select(driver.findElement(By.className("product_sort_container")));
        sortElement.selectByValue("lohi");
        selectProductNo(3);
        selectProductNo(4);

        logger.debug("no of items in cart:" + driver.findElement(By.className("shopping_cart_badge")).getText());
        clickButton(driver.findElement(By.className("shopping_cart_link")));//clicking cart
        clickButton(driver.findElement(By.id("checkout")));

        //checkout step one page
        webPage.sendText(driver.findElement(By.id("first-name")), "Sindhu");
        webPage.sendText(driver.findElement(By.id("last-name")), "Sukumaran");
        webPage.sendText(driver.findElement(By.id("postal-code")), "94551");
        webPage.clickButton(driver.findElement(By.id("continue")));

        //checkout-overview
        webPage.clickButton(driver.findElement(By.id("finish")));
    }

    @AfterSuite
    public void afterSuite() {
        driver.quit();
    }

    public void selectProductNo(int i) {
        WebElement item = driver.findElements(By.className("inventory_item")).get(i - 1);
        logger.debug("Price:" + item.findElement(By.className("inventory_item_price")).getText());
        item.findElement(By.cssSelector(".btn.btn_primary.btn_small.btn_inventory ")).click();
    }

    public void clickButton(WebElement btn) {
        btn.click();
    }

    public String usersFromPage(String credentials) {
        String line = driver.findElement(By.id("login_credentials")).getText();
        String[] users = line.split("\\n");
        logger.debug(users.length);
        Assert.assertTrue(users.length > 1);
        if (credentials.equalsIgnoreCase("happyPath"))
            return users[1];
        return "";
    }

    public String passwordFromPage() {
        String line = driver.findElement(By.className("login_password")).getText();
        String[] passwords = line.split("\\n");
        logger.debug(passwords.length);
        Assert.assertTrue(passwords.length > 1);

        return passwords[1];
    }
}

