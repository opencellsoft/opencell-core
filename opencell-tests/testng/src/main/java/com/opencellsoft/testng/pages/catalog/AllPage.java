package com.opencellsoft.testng.pages.catalog;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;

/**
 * 
 * @author Maria AIT BRAHIM
 * 
 */

public class AllPage extends BasePage {
    
    /**
     * amount without taxe.
     */
    
    @FindBy(id = "formPricePlan:tabView:amountWithoutTax_number")
    private WebElement amountWithoutTaxNumber;
    
    /**
     *
     * Save Recurring charge template.
     */
    
    @FindBy(id = "reccuringChargeId:formButtonsCC:saveButtonWithMessage")
    private WebElement buttonSave;
    
    /**
     *
     * Code search.
     */
    
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeSearch;
    /**
     *
     * search button .
     */
    
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement bttnSearch;
    
    @FindBy(xpath = "[@id=\"datatable_results_data\"/tr[1]/td[2]/a]")
    private WebElement searchelemenet;
    
    /**
     * selling Country Selected Id.
     */
    @FindBy(css = "#tabView\\:formTax\\:sellingCountrySelectedId_label")
    private WebElement sellingCountrySelectedId;
    
    /**
     * selling Country Selected Id 2.
     */
    @FindBy(id = "tabView:formTax:sellingCountrySelectedId_2")
    private WebElement sellingCountrySelected;
    /**
     * country Selected Id.
     */
    @FindBy(id = "tabView:formTax:countrySelectedId")
    private WebElement countrySelectedId;
    
    /**
     * country Selected Id 2.
     */
    @FindBy(id = "tabView:formTax:countrySelectedId_2")
    private WebElement countrySelected;
    
    /**
     * tax Selected Id.
     */
    @FindBy(id = "tabView:formTax:taxSelectedId")
    private WebElement taxSelectedId;
    
    /**
     * tax Selected Id 2.
     */
    @FindBy(id = "tabView:formTax:taxSelectedId_2")
    private WebElement taxSelected;
    
    /**
     * start validity date.
     */
    @FindBy(id = "tabView:formTax:startValidityDate_input")
    private WebElement startValidityDate;
    
    /**
     * AllPage Constructor.
     * 
     * @param driver WebDriver
     * 
     */
    
    public AllPage(final WebDriver driver) {
        super(driver);
        
    }
    
    /**
     * 
     * click on catalog -> service management -> charges -> All.
     * 
     * @param driver WebDriver
     */
    
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:catalog"));
        moveMouse(configurationMenu);
        
        WebElement serviceMenu = driver.findElement(By.id("menu:serviceManagement"));
        moveMouse(serviceMenu);
        
        WebElement chargesMenu = driver.findElement(By.id("menu:charges"));
        moveMouse(chargesMenu);
        
        WebElement allMenu = driver.findElement(By.id("menu:chargeTemplates"));
        moveMouseAndClick(allMenu);
        
    }
    
    /**
     * search code of Recurring charge template, update a share level .
     * 
     * @param driver WebDriver
     * @throws InterruptedException
     */
    
    public void addTaxe(WebDriver driver) throws InterruptedException {
        // click on invoice sub category code
        
        WebElement codeSearch = driver.findElement(By
            .id("datatable_results:0:invoiceSubCategory_code_invoiceSubCategory_id_message_link"));
        moveMouseAndClick(codeSearch);
        
        WebElement taxElement = driver
            .findElement(By.xpath("/html/body/div[2]/div/div[2]/div/ul/li[2]/a"));
        moveMouseAndClick(taxElement);
        // selling country
        moveMouseAndClick(sellingCountrySelectedId);
        sellingCountrySelected.click();
        // country
        countrySelectedId.click();
        countrySelected.click();
        // tax
        taxSelectedId.click();
        taxSelected.click();
        // start Validity Date
        startValidityDate.click();
        startValidityDate.sendKeys("13/08/2018");
        WebElement addTaxBttn = driver.findElements(By.className("ui-button-text-only")).get(2);
        moveMouseAndClick(addTaxBttn);
        
    }
    
}
