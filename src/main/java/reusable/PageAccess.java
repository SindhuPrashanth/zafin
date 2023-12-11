package reusable;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.List;

public class PageAccess {
    public boolean checkIfElementExist(WebElement element){
        return checkIfElementExist(element);
    }
    public void clickButton(WebElement element){
        element.click();
    }
    public void sendText(WebElement element, String inputText){
        element.sendKeys(inputText);
    }
    public String getElementText(WebElement element){
        return element.getText();
    }
    public ArrayList<String> getMultipleText(WebElement element){
        ArrayList<String> textList = new ArrayList<>();
        return textList;
    }

}
