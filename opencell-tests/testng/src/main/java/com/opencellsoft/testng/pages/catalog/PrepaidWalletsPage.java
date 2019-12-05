package com.opencellsoft.testng.pages.catalog;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

/**
 * 
 * 
 * @author Maria AIT BRAHIM
 */

public class PrepaidWalletsPage extends BasePage {
    
    /**
     * code.
     */
    @FindBy(id = "walletTemplatId:code_txt")
    private WebElement code;
    
    /**
     * Description.
     */
    @FindBy(id = "walletTemplatId:description")
    private WebElement description;
    
    /**
     * low Balance Level.
     */
    @FindBy(id = "walletTemplatId:lowBalanceLevel_number")
    private WebElement lowBalanceLevel;
    
    /**
     * search button.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBttn;
    
    /**
     * code search.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeSearch;
    
    /**
     * button save.
     */
    @FindBy(id = "walletTemplatId:formButtonsCC:saveButton")
    private WebElement bttnSave;
    
    /**
     * button New.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement bttnNew;
    
    /**
     * Constructor.
     * 
     * @param driver WebDriver
     */
    
    public PrepaidWalletsPage(WebDriver driver) {
        super(driver);
        
    }
    
    /**
     * click on configuration -> Catalog -> Prepaid wallets.
     * 
     * @param driver WebDriver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:catalog"));
        moveMouseAndClick(configurationMenu);
        WebElement walletMenu = driver.findElement(By.id("menu:walletTemplates"));
        moveMouseAndClick(walletMenu);
    }
    
    /**
     * fill the Prepaid wallet Form.
     * 
     * @param driver WebDriver
     * @param data Map
     */
    public void fillFormCreate(WebDriver driver, Map<String, String> data) {
        bttnNew.click();
        // Code
        code.click();
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        // Description
        description.click();
        description.clear();
        description.sendKeys((String) data.get(Constants.DESCRIPTION));
        
        // low Balance Level
        lowBalanceLevel.click();
        lowBalanceLevel.clear();
        lowBalanceLevel.sendKeys("50");
        bttnSave.click();
        codeSearch.click();
        codeSearch.clear();
        codeSearch.sendKeys((String) data.get(Constants.CODE));
        searchBttn.click();
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement chartToDelete = driver
                    .findElement(By.id("datatable_results:0:code_id_message_link"));
                moveMouseAndClick(chartToDelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement deleteBttn = driver
                    .findElement(By.id("walletTemplatId:formButtonsCC:deletelink"));
                moveMouseAndClick(deleteBttn);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        confirmDelete.click();
    }
    
    /**
     * @return the code
     */
    public final WebElement getCode() {
        return code;
    }
    
    /**
     * @param code the code to set
     */
    public final void setCode(WebElement code) {
        this.code = code;
    }
    
    /**
     * @return the description
     */
    public final WebElement getDescription() {
        return description;
    }
    
    /**
     * @param description the description to set
     */
    public final void setDescription(WebElement description) {
        this.description = description;
    }
    
}
