package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import reusable.BrowserCall;
import reusable.PageAccess;

public class GoogleClass {
//    public static void main(String[] args){
//        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
//        WebDriver driver = new ChromeDriver();
//        driver.get("https://www.google.com/");
//        //driver.findElement(By.name("q")).sendKeys("madurai"+ Keys.ENTER);
//        driver.findElement(By.xpath("//input[@name='q']")).sendKeys("madurai"+ Keys.ENTER);
//        driver.findElement(By.partialLinkText("Government")).click();
//    }

    @Test
    public void testMethod(){
        WebDriver driver = BrowserCall.createDriver();
        driver.get("https://www.saucedemo.com/");
        WebElement userName = driver.findElement(By.id("user-name"));
        WebElement passWordText = driver.findElement(By.id("password"));
        PageAccess homePage = new PageAccess();
        homePage.sendText(userName,"standard_user");
        homePage.sendText(passWordText,"secret_sauce");
    }
}
