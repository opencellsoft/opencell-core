package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * @author Maria AIT BRAHIM
 *
 */
public class PricePlansPage extends BasePage {
    
    /**
     * code.
     */
    @FindBy(id = "formPricePlan:tabView:pricePlanCode")
    private WebElement code;
    
    /**
     * description.
     */
    @FindBy(id = "formPricePlan:tabView:description")
    private WebElement description;
    /**
     * start Subscription Date.
     */
    @FindBy(id = "formPricePlan:tabView:startSubscriptionDate_date_input")
    private WebElement startSubscriptionDate;
    
    /**
     * selected row for delete.
     */
    @FindBy(id = "datatable_results:0:subject_id_message_link")
    private WebElement deleteRow;
    
    /**
     * button delete.
     */
    @FindBy(id = "formPricePlan:formButtonsCC:deletelink")
    private WebElement deleteBttn;
    /**
     * button New.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement bttnNew;
    
    /**
     * code search.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeSearch;
    /**
     * button search.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBttn;
    
    /**
     * 
     * @param driver instance of WebDriver
     */
    public PricePlansPage(final WebDriver driver) {
        super(driver);
        
    }
    
    /**
     * click on catalog -> price plans -> Price plans.
     * 
     * @param driver WebDriver
     * 
     */
    public void gotoListPage(final WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:catalog"));
        moveMouse(configurationMenu);
        
        WebElement pricePlanMenu = driver.findElement(By.id("menu:pricePlanMatrixes_menu"));
        moveMouse(pricePlanMenu);
        
        WebElement pricePlansMenu = driver.findElement(By.id("menu:pricePlanMatrixes"));
        moveMouseAndClick(pricePlansMenu);
        
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
     * fill the new Price plan.
     * 
     * @param driver instance of WebDriver
     * @param data Map
     * @throws InterruptedException
     */
    
    public void fillFormCreate(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        bttnNew.click();
        WebElement chargeList = driver.findElement(By.id("formPricePlan:tabView:pricPlanMatChargePopup_selectLink"));
        moveMouseAndClick(chargeList);
        WebElement charge =driver.findElement(By.xpath("/html/body/div[10]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]"));
        moveMouseAndClick(charge);
        WebElement amountWithoutTax = driver.findElement(By.id("formPricePlan:tabView:amountWithoutTax_number"));
        moveMouseAndClick(amountWithoutTax);
        amountWithoutTax.sendKeys("100");
        // Code
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        // Description
        moveMouseAndClick(description);
        description.clear();
        description.sendKeys((String) data.get(Constants.DESCRIPTION));
        WebElement bttnSave = driver.findElement(By.id("formPricePlan:formButtonsCC:saveButtonAjax"));
        moveMouseAndClick(bttnSave);
        moveMouseAndClick(codeSearch);
        codeSearch.clear();
        codeSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(searchBttn);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement deleteRow = driver
                    .findElement(By.id("datatable_results:0:pp_detail"));
                moveMouseAndClick(deleteRow);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        moveMouseAndClick(deleteBttn);
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
