package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * @author Maria AIT BRAHIM
 *
 */
public class ServicePage extends BasePage {
    
    /**
     * code.
     */
    @FindBy(id = "tabView:formId:code_txt")
    private WebElement code;
    
    /**
     * description.
     */
    @FindBy(id = "tabView:formId:description_txt")
    private WebElement description;
    
    /**
     * long Description.
     */
    @FindBy(id = "tabView:formId:longDescription")
    private WebElement longDescription;
    
    /**
     * minimum Amount El.
     */
    @FindBy(id = "tabView:formId:minimumAmountEl_txt")
    private WebElement minimumAmountEl;
    
    /**
     * minimum Label El.
     */
    @FindBy(id = "tabView:formId:minimumLabelEl_txt")
    private WebElement minimumLabelEl;
    
    /**
     * invoicing Calendar entity .
     */
    @FindBy(id = "tabView:formId:invoicingCalendar_entity")
    private WebElement invoicingCalendarEntity;
    
    /**
     * select Invoicing Calendar Entity.
     */
    @FindBy(id = "tabView:formId:invoicingCalendar_entity_2")
    private WebElement selectInvoicingCalendarEntity;
    
    /**
     * button delete.
     */
    @FindBy(id = "tabView:formId:formButtonsCC:deletelink")
    private WebElement deleteBttn;
    
    /**
     * button save.
     */
    @FindBy(id = "tabView:formId:formButtonsCC:saveButtonAjax")
    private WebElement bttnSave;
    
    /**
     * button New.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement bttnNew;
    
    /**
     * 
     * @param driver instance of WebDriver
     */
    public ServicePage(final WebDriver driver) {
        super(driver);
        
    }
    
    /**
     * click on catalog -> service management -> Service.
     * 
     * @param driver WebDriver
     * 
     */
    public void gotoListPage(final WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:catalog"));
        moveMouse(configurationMenu);
        
        WebElement serviceManagementMenu = driver.findElement(By.id("menu:serviceManagement"));
        moveMouse(serviceManagementMenu);
        
        WebElement serviceMenu = driver.findElement(By.id("menu:serviceTemplates"));
        moveMouseAndClick(serviceMenu);
        
    }
    
    /**
     * fill the new Service.
     * 
     * @param driver instance of WebDriver
     * @param data Map
     * @throws InterruptedException
     */
    
    public void fillFormCreate(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        bttnNew.click();
        // Code
        getCode().click();
        getCode().clear();
        getCode().sendKeys((String) data.get(Constants.CODE));
        
        // long Description
        getLongDescription().click();
        getLongDescription().clear();
        getLongDescription().sendKeys((String) data.get(Constants.LONG_DESCRIPTION));
        
        // minimum Label El.
        minimumLabelEl.click();
        minimumLabelEl.clear();
        minimumLabelEl.sendKeys("minutes");
        
        // minimum Amount El
        minimumAmountEl.click();
        minimumAmountEl.clear();
        minimumAmountEl.sendKeys("minutes");
        
        // Description
        getDescription().click();
        getDescription().clear();
        getDescription().sendKeys((String) data.get(Constants.DESCRIPTION));
        
        // select Invoicing Calendar Entity
        invoicingCalendarEntity.click();
        selectInvoicingCalendarEntity.click();
        bttnSave.click();
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
    
    /**
     * @return the longDescription
     */
    public final WebElement getLongDescription() {
        return longDescription;
    }
    
    /**
     * @param longDescription the longDescription to set
     */
    public final void setLongDescription(WebElement longDescription) {
        this.longDescription = longDescription;
    }
    
}
