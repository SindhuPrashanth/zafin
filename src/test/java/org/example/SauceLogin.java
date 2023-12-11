package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import reusable.BrowserCall;
import reusable.PageAccess;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.lang.Thread.sleep;

public class SauceLogin {

    private static final Logger logger = LogManager.getLogger(SauceLogin.class);
    public WebDriver driver = BrowserCall.createDriver();
    public WebElement userName, passWordText, logonButton;
    public WebElement errContainer;
    public List<String> loginUserNameList;
    public static String pagePassword;

    public HashMap<String, String> cart;

    @BeforeTest
    public void beforeTest() {
        loadLoginPage();
        loginUserNameList = getUsersListFromPage();
        logger.debug("lodin user list size:" + loginUserNameList.size());
        pagePassword = passwordFromPage();
        cart = new HashMap<>();
        ;
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test all usernames and password login validity, creds fetched from page itself at runtime. No hardcoding")
    public void loginTest() throws InterruptedException {

        PageAccess webPage = new PageAccess();
        for (int i = 1; i < loginUserNameList.size(); i++) {
            loadLoginPage();
            String currentPage = driver.getCurrentUrl();
            webPage.sendText(userName, loginUserNameList.get(i));
            webPage.sendText(passWordText, pagePassword);
            webPage.clickButton(logonButton);
            sleep(3000);
            //verify success or failure here AND NAVIGATE BACK

            if (loginUserNameList.get(i).equalsIgnoreCase("locked_out_user")) {
                //the page remains the same
                Assert.assertTrue(currentPage.equalsIgnoreCase(driver.getCurrentUrl()));
                errContainer = driver.findElement(By.className("login-box"));
                String actualErrorStr = errContainer.findElements(By.xpath("//form/div")).get(2).getText();
                logger.debug("Actual err string:" + actualErrorStr);
                Assert.assertEquals(actualErrorStr, "Epic sadface: Sorry, this user has been locked out.");
            } else {
                Assert.assertFalse(currentPage.equalsIgnoreCase(driver.getCurrentUrl()));
                driver.navigate().back();
            }
        }
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test negative login scenarios, by fetching invalid creds from xml")
    @Parameters({"username", "password", "loginStatus"})
    public void negativeLoginTest(String username, String password, String loginStatus) throws InterruptedException {
        logger.debug("Login credentials:\nUserName:" + username + "\nPassword:" + password);
        loadLoginPage();
        PageAccess webPage = new PageAccess();
        webPage.sendText(userName, username);
        webPage.sendText(passWordText, password);
        webPage.clickButton(logonButton);
        logger.debug("no od divs:" + errContainer.findElements(By.xpath("//form/div")).size());
        String actualErrorStr = errContainer.findElements(By.xpath("//form/div")).get(2).getText();
        logger.debug("Actual err string:" + actualErrorStr);
        sleep(3000);

        Assert.assertEquals(actualErrorStr, loginStatus);
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test negative login scenarios, by fetching invalid creds from xml")
    @Parameters({"username", "password", "loginStatus"})
    public void failLoginTest(String username, String password, String loginStatus) throws InterruptedException {
        logger.debug("Login credentials:\nUserName:" + username + "\nPassword:" + password);
        loadLoginPage();
        PageAccess webPage = new PageAccess();
        webPage.sendText(userName, username);
        webPage.sendText(passWordText, password);
        webPage.clickButton(logonButton);
        logger.debug("no od divs:" + errContainer.findElements(By.xpath("//form/div")).size());
        String actualErrorStr = errContainer.findElements(By.xpath("//form/div")).get(2).getText();
        logger.debug("Actual err string:" + actualErrorStr);
        sleep(3000);
        this.takeSnapShot(driver, "failedLogin.png");
        Assert.assertEquals(actualErrorStr, loginStatus);
    }

    public static void takeSnapShot(WebDriver webdriver, String screenShotLoc) {
        try {
            TakesScreenshot takesScreenshot = ((TakesScreenshot) webdriver);
            File source = takesScreenshot.getScreenshotAs(OutputType.FILE);
            File destination = new File(screenShotLoc);
            FileUtils.copyFile(source, destination);
        } catch (WebDriverException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test cart sanity. Check if cart contains selected products and right prices.")
    public void cartSanityTest() throws InterruptedException {
        logger.info("cartSanityTest");
        loadLoginPage();
        PageAccess webPage = new PageAccess();
        logger.debug("current url :" + driver.getCurrentUrl());
        webPage.sendText(userName, loginUserNameList.get(1));
        webPage.sendText(passWordText, pagePassword);
        webPage.clickButton(logonButton);
        sleep(5000);

        //products page
        logger.debug("Product lists url:" + driver.getCurrentUrl());
        Select sortElement = new Select(driver.findElement(By.className("product_sort_container")));
        sortElement.selectByValue("lohi"); //sorting from low to high
        getProductNo(3);
        getProductNo(4);

        logger.debug("no of items in cart:" + driver.findElement(By.className("shopping_cart_badge")).getText());
        clickButton(driver.findElement(By.className("shopping_cart_link")));//clicking cart

        WebElement cartList = driver.findElement(By.className("cart_list"));
        List<WebElement> cartItems = cartList.findElements(By.className("cart_item"));
        for (WebElement eachCartItem : cartItems) {
            String prdName = eachCartItem.findElement(By.className("inventory_item_name")).getText();
            String prdPrice = eachCartItem.findElement(By.className("inventory_item_price")).getText();

            Assert.assertTrue(cart.containsKey(prdName));
            Assert.assertEquals(prdPrice, cart.get(prdName));
        }

        clickButton(driver.findElement(By.id("checkout")));

        Thread.sleep(1000);

        //checkout step one page
        webPage.sendText(driver.findElement(By.id("first-name")), "Sindhu");
        webPage.sendText(driver.findElement(By.id("last-name")), "Sukumaran");
        webPage.sendText(driver.findElement(By.id("postal-code")), "94551");
        webPage.clickButton(driver.findElement(By.id("continue")));

        Thread.sleep(1000);

        WebElement summaryInfo = driver.findElement(By.id("checkout_summary_container"));
        List<WebElement> summaryValueLabels = summaryInfo.findElements(By.className("summary_value_label"));
        logger.debug("Payment Information : " + summaryValueLabels.get(0));
        String summarySubtotal_Label = summaryInfo.findElement(By.className("summary_subtotal_label")).getText();
        String summaryTaxLabel = summaryInfo.findElement(By.className("summary_tax_label")).getText();
        String summaryTotalLabel = summaryInfo.findElement(By.className("summary_total_label")).getText();

        logger.debug("Price information");
        logger.debug("sub total : " + summarySubtotal_Label);
        logger.debug("tax : " + summaryTaxLabel);
        logger.debug("total : " + summaryTotalLabel);

        //checkout-overview
        webPage.clickButton(driver.findElement(By.id("finish")));

        Assert.assertEquals(driver.findElement(By.id("checkout_complete_container")).getText(), "Thank you for your order!\n" +
                "Your order has been dispatched, and will arrive just as fast as the pony can get there!\n" +
                "Back Home");
    }

    @AfterTest
    public void afterTest() {
        driver.quit();
    }

    public void getProductNo(int i) {
        WebElement item = driver.findElements(By.className("inventory_item")).get(i - 1);
        logger.debug("Price:" + item.findElement(By.className("inventory_item_price")).getText());
        item.findElement(By.cssSelector(".btn.btn_primary.btn_small.btn_inventory ")).click();
        String prodName = item.findElement(By.className("inventory_item_name")).getText();
        String prodPrice = item.findElement(By.className("inventory_item_price")).getText();
        logger.debug(prodName + " " + prodPrice);
        cart.put(prodName, prodPrice);
    }


    public void clickButton(WebElement btn) {
        btn.click();
    }

    public void loadLoginPage() {
        driver.get("https://www.saucedemo.com/");
        userName = driver.findElement(By.id("user-name"));
        passWordText = driver.findElement(By.id("password"));
        logonButton = driver.findElement(By.id("login-button"));
        errContainer = driver.findElement(By.className("login-box"));
    }

    public List<String> getUsersListFromPage() {
        String line = driver.findElement(By.id("login_credentials")).getText();
        String[] users = line.split("\\n");
        logger.debug(users.length);
        Assert.assertTrue(users.length > 1);
        List<String> usersList = Arrays.asList(users);
        logger.debug("userNames list:" + usersList);
        return usersList;
    }

    public String passwordFromPage() {
        String line = driver.findElement(By.className("login_password")).getText();
        String[] passwords = line.split("\\n");
        logger.debug(passwords.length);
        Assert.assertTrue(passwords.length > 1);

        return passwords[1];
    }
}

