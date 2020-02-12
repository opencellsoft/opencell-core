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
 * @author MIFTAH
 *
 */
public class ModulePage extends BasePage {
    
    /**
     * code.
     */
    @FindBy(id = "moduleForm:code_txt")
    private WebElement code;
    
    /**
     * description.
     */
    @FindBy(id = "moduleForm:description")
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
    @FindBy(id = "searchForm:buttonNew")
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
        WebElement administrationMenu = driver.findElement(By.id("menu:automation"));
        moveMouse(administrationMenu);
        WebElement moduleMenu = driver.findElement(By.id("menu:meveoModules"));
        moveMouseAndClick(moduleMenu);
        
    }
    
    /**
     * click on Modules.
     * 
     * @param driver WebDriver
     */
    
    public void gotoNewPage(WebDriver driver) {
        moveMouseAndClick(bttnNew);
    }
    
    /**
     * the button save.
     * 
     * @param driver WebDriver
     * @throws InterruptedException
     */
    public void save(WebDriver driver) throws InterruptedException {
        WebElement savebttn = driver.findElements(By.className("ui-button-text-icon-left")).get(0);
        moveMouseAndClick(savebttn);
        WebElement backbttn = driver.findElements(By.className("ui-button-text-only")).get(7);
        moveMouseAndClick(backbttn);
    }
    
    /**
     * fill the new Module.
     * 
     * @param driver instance of WebDriver
     * @param data Map
     */
    public void fillFormCreate(WebDriver driver, Map<String, String> data) {
        // Code
        moveMouseAndClick(getCode());
        getCode().clear();
        getCode().sendKeys((String) data.get(Constants.CODE));
        // Description
        moveMouseAndClick(getDescription());
        getDescription().clear();
        getDescription().sendKeys((String) data.get(Constants.DESCRIPTION));
        // select license
        moveMouseAndClick(license);
        moveMouseAndClick(licenseSelected);
    }
    
    /**
     * search for the new created Module.
     * 
     * @param driver instance of WebDriver
     * @param data Map
     */
    public void fillFormAndSearch(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(codeSearch);
        codeSearch.clear();
        codeSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(searchBttnCtp);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement deletebttn = driver
                    .findElement(By.id("datatable_results:0:resultsdeletelink"));
                moveMouseAndClick(deletebttn);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
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
