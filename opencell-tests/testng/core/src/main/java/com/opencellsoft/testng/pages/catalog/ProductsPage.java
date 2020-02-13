package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * @author HASSNAA MIFTAH 
 *
 */
public class ProductsPage extends BasePage {

    
    /**
     * Product's name
     */
    @FindBy(id = "tabView:formId:name")
    private WebElement name;
    
    /**
     * date of validity (from)
     */
    @FindBy(id = "tabView:formId:validity_date_input")
    private WebElement validFrom;
    
    /**
     * date of validation (to)
     */
    @FindBy(id = "tabView:formId:validity_date_toRange_input")
    private WebElement validTo;
    
    /**
     * life Cycle Status
     */
    @FindBy(id = "tabView:formId:lifeCycleStatus_label")
    private WebElement lifeCycleStatus;
    
    /**
     * life Cycle Status:inDesign
     */
    @FindBy(id = "tabView:formId:lifeCycleStatus_1")
    private WebElement inDesign;
    
    /**
     * category
     */
    @FindBy(id = "tabView:formId:offerTemplateCategories_label")
    private WebElement category;
    
    /**
     * channels
     */
    @FindBy(id = "tabView:formId:channels_label")
    private WebElement channels;
    
    /**
     * target Segment
     */
    @FindBy(id = "tabView:formId:businessAccountModels_label")
    private WebElement targetSegment;
    
    /**
     * limited To Sellers
     */
    @FindBy(id = "tabView:formId:sellers")
    private WebElement limitedToSellers;
    
    /**
     * calendar
     */
    @FindBy(id = "tabView:formId:invoicingCalendar_entity_label")
    private WebElement calendar;
    
    /**
     * description
     */
    @FindBy(id = "tabView:formId:description")
    private WebElement description;
    
    /**
     * long Description
     */
    @FindBy(id = "tabView:formId:longDescription")
    private WebElement longDescription;
    /**
     * Constructor
     * 
     * @param driver WebDriver
     */
    public ProductsPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * go to Catalog > Product Management > product
     * 
     * @param driver WebDriver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:catalog"));
        moveMouse(configurationMenu);
        
        WebElement productManagementMenu = driver.findElement(By.id("menu:productManagement"));
        moveMouseAndClick(productManagementMenu);
        
        WebElement productMenu = driver.findElement(By.id("menu:productTemplates"));
        moveMouseAndClick(productMenu);
        
    }
    /**
     * fillForm method
     * 
     * @param driver WebDriver
     * @param data Map
     * @throws InterruptedException Exception
     */
    public void fillForm(WebDriver driver, Map<String, String> data) throws InterruptedException {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
        WebElement code = driver.findElement(By.id("tabView:formId:code_txt"));
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        name.click();
        name.clear();
        name.sendKeys((String) data.get(Constants.CODE));
        validFrom.click();
        validFrom.clear();
        validFrom.sendKeys("01/05/2019");
        
        validTo.click();
        validTo.clear();
        validTo.sendKeys("30/12/2019");
                
        lifeCycleStatus.click();
        inDesign.click();
        
        description.click();
        description.clear();
        description.sendKeys("this is a description ");
        
        longDescription.click();
        longDescription.clear();
        longDescription.sendKeys("this is a long description ");
        
        WebElement btnSave = driver.findElement(By.id("tabView:formId:formButtonsCC:saveButton"));
        moveMouseAndClick(btnSave);
        /**
         * fill the code.
         */
        WebElement codeToSearch = driver.findElement(By.id("searchForm:code_txt"));
        moveMouseAndClick(codeToSearch);
        codeToSearch.clear();
        codeToSearch.sendKeys((String) data.get(Constants.CODE));
        
        /**
         * click on search button.
         */
        WebElement searchCp = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(searchCp);
        
        /**
         * click on the element searched.
         */
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
                WebElement delete = driver
                    .findElement(By.id("tabView:formId:formButtonsCC:deletelink"));
                moveMouseAndClick(delete);
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
        confirmDelete.click();
        
    }

    /**
     * @return the name
     */
    public WebElement getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(WebElement name) {
        this.name = name;
    }

    /**
     * @return the validFrom
     */
    public WebElement getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(WebElement validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the validTo
     */
    public WebElement getValidTo() {
        return validTo;
    }

    /**
     * @param validTo the validTo to set
     */
    public void setValidTo(WebElement validTo) {
        this.validTo = validTo;
    }

    /**
     * @return the lifeCycleStatus
     */
    public WebElement getLifeCycleStatus() {
        return lifeCycleStatus;
    }

    /**
     * @param lifeCycleStatus the lifeCycleStatus to set
     */
    public void setLifeCycleStatus(WebElement lifeCycleStatus) {
        this.lifeCycleStatus = lifeCycleStatus;
    }

    /**
     * @return the inDesign
     */
    public WebElement getInDesign() {
        return inDesign;
    }

    /**
     * @param inDesign the inDesign to set
     */
    public void setInDesign(WebElement inDesign) {
        this.inDesign = inDesign;
    }

    /**
     * @return the category
     */
    public WebElement getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(WebElement category) {
        this.category = category;
    }

    /**
     * @return the channels
     */
    public WebElement getChannels() {
        return channels;
    }

    /**
     * @param channels the channels to set
     */
    public void setChannels(WebElement channels) {
        this.channels = channels;
    }

    /**
     * @return the targetSegment
     */
    public WebElement getTargetSegment() {
        return targetSegment;
    }

    /**
     * @param targetSegment the targetSegment to set
     */
    public void setTargetSegment(WebElement targetSegment) {
        this.targetSegment = targetSegment;
    }

    /**
     * @return the limitedToSellers
     */
    public WebElement getLimitedToSellers() {
        return limitedToSellers;
    }

    /**
     * @param limitedToSellers the limitedToSellers to set
     */
    public void setLimitedToSellers(WebElement limitedToSellers) {
        this.limitedToSellers = limitedToSellers;
    }

    /**
     * @return the calendar
     */
    public WebElement getCalendar() {
        return calendar;
    }

    /**
     * @param calendar the calendar to set
     */
    public void setCalendar(WebElement calendar) {
        this.calendar = calendar;
    }

    /**
     * @return the description
     */
    public WebElement getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(WebElement description) {
        this.description = description;
    }

    /**
     * @return the longDescription
     */
    public WebElement getLongDescription() {
        return longDescription;
    }

    /**
     * @param longDescription the longDescription to set
     */
    public void setLongDescription(WebElement longDescription) {
        this.longDescription = longDescription;
    }

}