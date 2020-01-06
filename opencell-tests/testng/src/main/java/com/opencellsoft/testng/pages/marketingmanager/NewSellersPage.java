package com.opencellsoft.testng.pages.marketingmanager;

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

public class NewSellersPage extends BasePage {
    
    /**
     * code
     */
    @FindBy(id = "sellerFormId:tabView:code_txt")
    private WebElement codeS;
    
    /**
     * description
     */
    @FindBy(id = "sellerFormId:tabView:description")
    private WebElement descriptionS;
    
    /**
     * button new
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement btnNewS;
    
    /**
     * country
     */
    @FindBy(id = "sellerFormId:tabView:trCountrySelectId_label")
    private WebElement country;
    
    /**
     * country choice
     */
    @FindBy(id = "sellerFormId:tabView:trCountrySelectId_items")
    private WebElement countrychoice;
    
    /**
     * language
     */
    @FindBy(id = "sellerFormId:tabView:trLanguageSelectId_label")
    private WebElement language;
    
    /**
     * language choice
     */
    @FindBy(id = "sellerFormId:tabView:trLanguageSelectId_items")
    private WebElement languagechoice;
    
    /**
     * currency
     */
    @FindBy(id = "sellerFormId:tabView:trCurrencySelectId_label")
    private WebElement currency;
    
    /**
     * currency choice
     */
    @FindBy(id = "sellerFormId:tabView:trCurrencySelectId_items")
    private WebElement currencychoice;
    
    /**
     * parent
     */
    @FindBy(id = "sellerFormId:tabView:parentSelectId_label")
    private WebElement parent;
    
    /**
     * parent choice
     */
    @FindBy(id = "sellerFormId:tabView:parentSelectId_items")
    private WebElement parentchoice;
    
    /**
     * addNew button
     */
    @FindBy(id = "sellerFormId:tabView:j_idt199")
    private WebElement addNew;
    
    /**
     * email
     */
    @FindBy(id = "sellerFormId:tabView:email")
    private WebElement email;
    
    /**
     * phone
     */
    @FindBy(id = "sellerFormId:tabView:contactInformation_phone_txt")
    private WebElement phone;
    
    /**
     * mobile
     */
    @FindBy(id = "sellerFormId:tabView:contactInformation_mobile_txt")
    private WebElement mobile;
    
    /**
     * address1
     */
    @FindBy(id = "sellerFormId:tabView:address1")
    private WebElement address1;
    
    /**
     * zip code
     */
    @FindBy(id = "sellerFormId:tabView:zipCode")
    private WebElement zipcode;
    
    /**
     * city
     */
    @FindBy(id = "sellerFormId:tabView:city")
    private WebElement city;
    
    /**
     * country inf
     */
    @FindBy(id = "sellerFormId:tabView:countryID_label")
    private WebElement countryinf;
    
    /**
     * country inf click
     */
    @FindBy(id = "sellerFormId:tabView:countryID_1")
    private WebElement countryinfclick;
    
    /**
     * invoiceTypeCB button
     */
    @FindBy(xpath = "//form[@id='sellerSequenceChgPopup']/descendant::label[contains(@id,'sellerSequenceChgPopup') and contains(@id,'_label')]")
    private WebElement invoiceTypeCB;
    
    /**
     * prefix
     */
    @FindBy(id = "sellerSequenceChgPopup:j_idt380")
    private WebElement prefix;
    
    /**
     * saveApp button
     */
    @FindBy(id = "sellerSequenceChgPopup:j_idt382")
    private WebElement saveApp;
    
    /**
     * constructor
     * 
     * @param driver WebDriver
     */
    public NewSellersPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * go to Configuration -> sellers
     * 
     * @param driver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        Actions action = new Actions(driver);
        action.moveToElement(configurationMenu).build().perform();
        
        WebElement sellersMenu = driver.findElement(By.id("menu:sellers"));
        action = new Actions(driver);
        action.moveToElement(sellersMenu).build().perform();
        
        sellersMenu.click();
        
    }
    
    /**
     * go to new page method
     * 
     * @param driver WebDriver
     */
    public void gotoNewPage(WebDriver driver) {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        btnNew.click();
    }
    
    /**
     * save method .
     * 
     * @param driver
     */
    public void gotoSave(WebDriver driver) {
        WebElement btnSave = driver.findElement(By.id("sellerFormId:formButtonsCC:saveButtonAjax"));
        btnSave.click();
        
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
        String test = "MH_" + System.currentTimeMillis();
        
        codeS.click();
        codeS.clear();
        codeS.sendKeys(test);
        
        descriptionS.click();
        descriptionS.clear();
        descriptionS.sendKeys("this is a description");
        
        country.click();
        countrychoice.click();
        language.click();
        languagechoice.click();
        currency.click();
        currencychoice.click();
        parent.click();
        parentchoice.click();
        WebElement parentElement0 = driver.findElement(By.className("ui-tabs-panels"));
        WebElement addNew = parentElement0.findElement(By.tagName("button"));
        addNew.click();
        Thread.sleep(2000);
        WebElement invoiceType = driver.findElements(By.className("ui-corner-right")).get(5);
        
        invoiceType.click();
        
        WebElement invoiceTypeItems = driver.findElement(
            By.xpath("//li[contains(@id,'sellerSequenceChgPopup') and contains(@id,'_1')]"));
        invoiceTypeItems.click();
        // invoiceTypeItems
        WebElement currentinvoicenumber = driver.findElements(By.className("ui-spinner-input"))
            .get(0);
        currentinvoicenumber.click();
        currentinvoicenumber.clear();
        currentinvoicenumber.sendKeys("12");
        
        // WebElement currentinvoicenumber = driver.findElements(By.tagName("input")).get(33);
        WebElement sequenceSize = driver.findElements(By.className("ui-spinner-input")).get(1);
        sequenceSize.click();
        sequenceSize.clear();
        sequenceSize.sendKeys("15");
        /*
        WebElement prefixEl = driver.findElements(By.className("ui-inputfield")).get(18);
        
        prefixEl.click();
        prefixEl.clear();
        prefixEl.sendKeys("prefix ");
        */
        WebElement saveApp = driver.findElements(By.className("ui-button-text-only")).get(7);
        saveApp.click();
        
        WebElement parentElement7 = driver.findElement(
            By.xpath("/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/ul/li[2]/a"));
        forceClick(parentElement7);
        
        WebElement info = driver.findElement(By.className("ui-tabs-selected"));
        info.click();
        Thread.sleep(2000);
    }
    
    /**
     * method to fill form named info
     * 
     * @param driver
     * @param data
     */
    public void fillFormInfo(WebDriver driver, Map<String, String> data) {
        WebElement email = driver.findElement(By.id("sellerFormId:tabView:email"));
        forceSendkeys(driver, email, "miftah.hassnaa@opencell.com");
        
        WebElement phone = driver
            .findElement(By.id("sellerFormId:tabView:contactInformation_phone_txt"));
        forceSendkeys(driver, phone, "212060666");
        
        WebElement mobile = driver
            .findElement(By.id("sellerFormId:tabView:contactInformation_mobile_txt"));
        forceSendkeys(driver, mobile, "05654522");
        
        WebElement address1 = driver.findElement(By.id("sellerFormId:tabView:address1"));
        forceSendkeys(driver, address1, "adress1");
        
        WebElement zipcode = driver.findElement(By.id("sellerFormId:tabView:zipCode"));
        forceSendkeys(driver, zipcode, "123456");
        
        WebElement city = driver.findElement(By.id("sellerFormId:tabView:city"));
        forceSendkeys(driver, city, "paris");
    }
    
    
}
