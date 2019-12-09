package com.opencellsoft.testng.pages.configuration;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;

/**
 * @author MIFTAH HASSNAA
 *
 */

public class SellersPage extends BasePage {
    
    /**
     * description
     */
    @FindBy(id = "sellerFormId:tabView:description")
    private WebElement descriptionS;
    
    /**
     * constructor
     * 
     * @param driver WebDriver
     */
    
    public SellersPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * go to Configuration -> sellers
     * 
     * @param driver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouseAndClick(configurationMenu);
        WebElement sellersMenu = driver.findElement(By.id("menu:sellers"));
        moveMouseAndClick(sellersMenu);
    }
    
    /**
     * method to force filling fields .
     * 
     * @param driver
     * @param element
     * @param keysToSend
     */
    public void forceSendkeys(WebDriver driver, WebElement element, String keysToSend) {
        Actions action = new Actions(driver);
        action.moveToElement(element).click().sendKeys(keysToSend).perform();
    }
    
    /**
     * fillForm method.
     * 
     * @param driver
     * @param data
     * @throws InterruptedException
     */
    public void fillForm(WebDriver driver, Map<String, String> data) throws InterruptedException {
        String test = "SE_" + System.currentTimeMillis();        
        WebElement btnNew =driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
        WebElement codeS =driver.findElement(By.id("parentTab:sellerFormId:tabView:code_txt"));
        codeS.sendKeys(test);
        WebElement descriptionS = driver.findElement(By.id("parentTab:sellerFormId:tabView:description"));
        descriptionS.sendKeys(test);
        WebElement sellerTab = driver.findElement(By.xpath("/html/body/div[2]/span/span/div/div/div/form/div/div[2]/div/div[2]/div/ul/li[2]/a"));
        moveMouseAndClick(sellerTab);
        WebElement email = driver.findElement(By.id("parentTab:sellerFormId:tabView:email"));
        forceSendkeys(driver, email, "miftah.hassnaa@opencell.com");
        WebElement phone = driver
            .findElement(By.id("parentTab:sellerFormId:tabView:contactInformation_phone_txt"));
        forceSendkeys(driver, phone, "212060666");
        WebElement mobile = driver
            .findElement(By.id("parentTab:sellerFormId:tabView:contactInformation_mobile_txt"));
        forceSendkeys(driver, mobile, "05654522");
        WebElement btnSave = driver.findElement(By.id("parentTab:sellerFormId:formButtonsCC:saveButtonAjax"));
        moveMouseAndClick(btnSave);
        WebElement searchCodeCP =driver.findElement(By.id("searchForm:seller"));
        moveMouseAndClick(searchCodeCP);
        searchCodeCP.clear();
        searchCodeCP.sendKeys(test);
        WebElement searchCP = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(searchCP);
        WebElement elementToDelete =driver.findElement(By.id("datatable_results:0:code_id_message_link"));
        moveMouseAndClick(elementToDelete);
        WebElement delete = driver.findElement(By.id("parentTab:sellerFormId:formButtonsCC:deletelink"));
        moveMouseAndClick(delete);
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
        
    }
}
