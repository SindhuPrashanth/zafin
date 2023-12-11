package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import reusable.BrowserCall;
import reusable.PageAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class SauceLogin {
    public WebDriver driver = BrowserCall.createDriver();
    public WebElement userName, passWordText, logonButton;
    public WebElement errContainer;
    public List<String> loginUserNameList;
    public static String pagePassword;
    @BeforeTest
    public void beforeTest(){
        loadLoginPage();
        loginUserNameList = getUsersListFromPage();
        System.out.println("lodin user list size:"+loginUserNameList.size());
        pagePassword = passwordFromPage();
    }

//    @DataProvider(name = "loginTest")
//    public static Object[][] testData() {
//        Object[][] data = new Object[loginUserNameList.size()-1][3];
//        for(int i=0; i<loginUserNameList.size()-1; i++)
//            data[i] = new Object[][]{{loginUserNameList.get(i+1).toString(),"SUCCESS"}};
//        return data;
//    }

    // This test will run 4 times since we have 5 parameters defined
//    @Test(dataProvider = "loginTest")
    @Test
    public void loginSauce() throws InterruptedException {

        PageAccess webPage = new PageAccess();
        //String userNameInput  = driver.findElement(By.xpath("")).getText();
        for(int i=1;i<loginUserNameList.size();i++) {
            loadLoginPage();
            String currentPage = driver.getCurrentUrl();
            webPage.sendText(userName, loginUserNameList.get(i));
            webPage.sendText(passWordText, pagePassword);
            webPage.clickButton(logonButton);
            sleep(5000);
            //verify success or failure here AND NAVIGATE BACK

            if(loginUserNameList.get(i).equalsIgnoreCase("locked_out_user")){
                //the page remains the same
                Assert.assertTrue(currentPage.equalsIgnoreCase(driver.getCurrentUrl()));
                errContainer = driver.findElement(By.className("login-box"));
                String actualErrorStr = errContainer.findElements(By.xpath("//form/div")).get(2).getText();
                System.out.println("Actual err string:"+actualErrorStr);
                Assert.assertEquals(actualErrorStr, "Epic sadface: Sorry, this user has been locked out.");
            }
            else {
                Assert.assertFalse(currentPage.equalsIgnoreCase(driver.getCurrentUrl()));
                driver.navigate().back();
            }
        }
    }
    @Test
    @Parameters({ "username", "password", "loginStatus"})
    public void negativeLoginTest(String username, String password, String loginStatus) throws InterruptedException {
        System.out.println("Login credentials:\nUserName:"+ username+"\nPassword:"+password);
        loadLoginPage();
        PageAccess webPage = new PageAccess();
        webPage.sendText(userName, username);
        webPage.sendText(passWordText, password);
        webPage.clickButton(logonButton);
        System.out.println("no od divs:"+errContainer.findElements(By.xpath("//form/div")).size());
        String actualErrorStr = errContainer.findElements(By.xpath("//form/div")).get(2).getText();
        System.out.println("Actual err string:"+actualErrorStr);
        sleep(5000);

        Assert.assertEquals(actualErrorStr,loginStatus);
    }
    @AfterTest
    public void afterTest(){
        driver.quit();
    }

    public void clickButton(WebElement btn){
        btn.click();
    }
    public void loadLoginPage(){
        driver.get("https://www.saucedemo.com/");
        userName = driver.findElement(By.id("user-name"));
        passWordText = driver.findElement(By.id("password"));
        logonButton = driver.findElement(By.id("login-button"));
        errContainer = driver.findElement(By.className("login-box"));
    }
    public List<String> getUsersListFromPage() {
        String line = driver.findElement(By.id("login_credentials")).getText();
        String[] users = line.split("\\n");
        System.out.println(users.length);
        Assert.assertTrue(users.length > 1);
        List<String> usersList = Arrays.asList(users);
//        for(int i=1; i<users.length;i++)
//            usersList.add(users[i]);
       System.out.println("userNames list:"+usersList);
        return usersList;
    }

    public String passwordFromPage(){
        String line = driver.findElement(By.className("login_password")).getText();
        String[] passwords = line.split("\\n");
        System.out.println(passwords.length);
        Assert.assertTrue(passwords.length > 1);

        return passwords[1];
    }
}

