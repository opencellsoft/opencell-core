package com.opencellsoft.testng.pages.catalog;

import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class BundlesPage  extends BasePage {


    /**
     * constructor .
     * 
     * @param driver WebDriver
     */
    public BundlesPage(WebDriver driver) {
        super(driver);
    }
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:catalog"));
        moveMouse(configurationMenu);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement menuproductManagement = driver
                    .findElement(By.id("menu:productManagement"));
                moveMouseAndClick(menuproductManagement);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        WebElement menubundleTemplates = driver.findElement(By.id("menu:bundleTemplates"));
        moveMouseAndClick(menubundleTemplates);
        
    }
    
    public void fillInformationsForm(WebDriver driver, Map<String, String> data) throws InterruptedException {
        
        WebElement buttonNew =driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(buttonNew);
        WebElement code = driver.findElement(By.id("formId:code_txt"));
       	code.click();
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        WebElement name = driver.findElement(By.id("formId:name"));
        name.click();
        name.clear();
        name.sendKeys((String) data.get(Constants.CODE));
        WebElement saveButton = driver.findElement(By.id("formId:formButtonsCC:saveButton"));
        moveMouseAndClick(saveButton);
        WebElement searchCode = driver.findElement(By.id("searchForm:code_txt"));
        searchCode.click();
        searchCode.clear();
        searchCode.sendKeys((String) data.get(Constants.CODE));
        WebElement buttonSearch = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(buttonSearch);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement datatableToDelete = driver
                    .findElement(By.id("datatable_results:0:code_id_message_link"));
                moveMouseAndClick(datatableToDelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        WebElement delete = driver.findElement(By.id("formId:formButtonsCC:deletelink"));
        moveMouseAndClick(delete);
        /**
         * click on confirm button.
         */
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        confirmDelete.click();
    }
}
