package com.opencellsoft.testng.pages.administration;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Maria AIT BRAHIM
 *
 */
public class ModulePage extends BasePage {
    
    /**
     * code.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div[1]/div/div/input")
    private WebElement code;
    
    /**
     * description.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div[2]/div/div/input")
    private WebElement description;
    
    /**
     * license.
     */
    @FindBy(id = "moduleForm:license")
    private WebElement license;
    
    /**
     * Selected license .
     */
    @FindBy(id = "moduleForm:license_4")
    private WebElement licenseSelected;
    
    /**
     * Module installation and activation script .
     */
    @FindBy(className = "ui-icon-search")
    private WebElement scriptsearch;
    
    /**
     * button search.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBttnCtp;
    
    /**
     * code search.
     */
    @FindBy(id = "searchForm:meveoModule")
    private WebElement codeSearch;
    
    /**
     * button New.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span")
    private WebElement bttnNew;
    
    /**
     * ModulePage constructor.
     * 
     * @param driver instance of WebDriver
     */
    public ModulePage(final WebDriver driver) {
        super(driver);
    }
    
    /**
     * click on administration -> Modules.
     * 
     * @param driver WebDriver
     * 
     */
    public void gotoListPage(final WebDriver driver) {
        WebElement adminstrationMenu = driver.findElement(By
            .xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[5]/span[2]"));
        moveMouse(adminstrationMenu);
        WebElement moduleMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[6]/div/div/a"));
        moveMouseAndClick(moduleMenu);
        
    }
    
    /**
     * click on Modules.
     * 
     * @param driver WebDriver
     */
    
    public void gotoNewPage(WebDriver driver) {
        
        bttnNew.click();
    }
    
    /**
     * the button save.
     * 
     * @param driver WebDriver
     * @throws InterruptedException
     */
    public void save(WebDriver driver) throws InterruptedException {
        WebElement savebttn = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button/span[1]"));
        forceClick(savebttn);
        
    }
    
    /**
     * fill the new Module.
     * 
     * @param driver instance of WebDriver
     * @param data Map
     */
    public void fillFormCreate(WebDriver driver, Map<String, String> data) {
        // Code
        
        getCode().click();
        getCode().clear();
        getCode().sendKeys((String) data.get(Constants.CODE));
        
        // Description
        getDescription().click();
        getDescription().clear();
        getDescription().sendKeys((String) data.get(Constants.DESCRIPTION));
        // select license
        
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
