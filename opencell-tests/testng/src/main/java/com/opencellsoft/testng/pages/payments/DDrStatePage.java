package com.opencellsoft.testng.pages.payments;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;

public class DDrStatePage extends BasePage {
    
    public DDrStatePage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * payment Menu.
     */
    @FindBy(id = "menu:payment")
    private WebElement paymentMenu;
    /**
     * directDebit Page.
     */
    @FindBy(id = "menu:directDebit")
    private WebElement directDebitPage;
    
    /**
     * directDebit Page.
     */
    @FindBy(id = "menu:ddrequestLotOps")
    private WebElement ddrequestLotOps;

    
    @FindBy(xpath = "/html/body/div[11]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement ddrequestBuilder;
    
    @FindBy(id = "FormId:formButtonsCC:saveButton")
    private WebElement saveButton;
    
    public void opendDrBuilderList(final WebDriver driver) throws InterruptedException {
        moveMouseAndClick(paymentMenu);
        moveMouseAndClick(directDebitPage);
        moveMouseAndClick(paymentMenu);
        moveMouseAndClick(ddrequestLotOps);
        
    }
    
    public void fillDDrBuildersAndSave(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement buttonNew = driver.findElement(By.id("searchForm:buttonNew"));
                moveMouseAndClick(buttonNew);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement ddrequestBuilderList = driver.findElement(By.id("FormId:builderSelectId_selectLink"));
                moveMouseAndClick(ddrequestBuilderList);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        moveMouseAndClick(ddrequestBuilder);
        moveMouseAndClick(saveButton);
        ///////////
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement rowTODelete = driver
                    .findElement(By.id("datatable_results:0:resultsdeletelink"));
                moveMouseAndClick(rowTODelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        /**
         * click on confirm button.
         */
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
    }
}
