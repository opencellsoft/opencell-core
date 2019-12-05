package com.opencellsoft.testng.pages.marketingmanager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.opencellsoft.testng.pages.BasePage;

/**
 * @author HASSNAA MIFTAH 
 *
 */
public class CustomersPage extends BasePage {
    
    /**
     * @param driver WebDriver.
     */
    public CustomersPage(WebDriver driver) {
        super(driver);
    }
    
    public void customersMenu(WebDriver driver) {
        WebElement customers = driver.findElement(
            By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[3]/a/span[1]"));
        moveMouse(customers);
    }
    
    public void globalSearch(WebDriver driver) {
        WebElement globalSearch = driver.findElement(
            By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[3]/ul/li[1]/a/span"));
        moveMouse(globalSearch);
    }
    
    public void customersCreation(WebDriver driver) throws InterruptedException {
        WebElement newBtn = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouse(newBtn);
        Thread.sleep(2000);
        WebElement customersCode = driver.findElement(By.id("formCustomer:tabView:code"));
        forceClick(customersCode);
        Thread.sleep(2000);
        WebElement customersCategory = driver
            .findElement(By.id("formCustomer:tabView:customerCategory_entity_label"));
        forceClick(customersCategory);
        Thread.sleep(2000);
        WebElement customersCategoryChoice = driver
            .findElement(By.id("formCustomer:tabView:customerCategory_entity_3"));
        forceClick(customersCategoryChoice);
        Thread.sleep(2000);
        WebElement parentSeller = driver
            .findElement(By.id("formCustomer:tabView:parentSelectId_label"));
        forceClick(parentSeller);
        Thread.sleep(2000);
        WebElement parentSellerChoice = driver
            .findElement(By.id("formCustomer:tabView:parentSelectId_7"));
        forceClick(parentSellerChoice);
        Thread.sleep(2000);
        WebElement saveBtn = driver.findElements(By.className("ui-button-text-icon-left")).get(0);
        forceClick(saveBtn);
        Thread.sleep(2000);
        
    }
    
    public void customerAccount(WebDriver driver) throws InterruptedException {
        
        WebElement customerAccount = driver.findElement(
            By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[3]/ul/li[3]/a/span"));
        moveMouse(customerAccount);
        Thread.sleep(8000);
        WebElement newBtn = driver.findElement(By.id("searchForm:buttonNew"));
        forceClick(newBtn);
        Thread.sleep(8000);
        
        WebElement customerAccountCode = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:code_txt"));
        forceClick(customerAccountCode);
        Thread.sleep(8000);
        
        WebElement currency = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:currencySelectId_label"));
        forceClick(currency);
        Thread.sleep(8000);
        
        WebElement currencyChoice = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:currencySelectId_1"));
        forceClick(currencyChoice);
        Thread.sleep(8000);
        
        WebElement language = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:trLanguageSelectId_label"));
        forceClick(language);
        Thread.sleep(8000);
        
        WebElement languageChoice = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:trLanguageSelectId_2"));
        forceClick(languageChoice);
        Thread.sleep(8000);
        
        WebElement saveBtn = driver.findElements(By.className("ui-button-text-icon-left")).get(0);
        forceClick(saveBtn);
        
    }
    
    public void billingAccount(WebDriver driver) throws InterruptedException {
        
        WebElement billingAccount = driver.findElement(By.xpath("menu:billingAccounts"));
        moveMouse(billingAccount);
        Thread.sleep(8000);
        WebElement newBtn = driver.findElement(By.id("searchForm:buttonNew"));
        forceClick(newBtn);
        Thread.sleep(8000);
        
        WebElement customerAccount = driver
            .findElement(By.id("parentTab:formId:childTab:customerSelectId_selectLink"));
        forceClick(customerAccount);
        Thread.sleep(8000);
        
        WebElement element = driver.findElement(By.linkText("ben.ohara"));
        element.click();
        Thread.sleep(8000);
        
        WebElement billingCycle = driver
            .findElement(By.id("parentTab:formId:childTab:cycleSelectId_label"));
        forceClick(billingCycle);
        Thread.sleep(8000);
        
        WebElement billingCycleChoice = driver
            .findElement(By.id("parentTab:formId:childTab:cycleSelectId_1"));
        forceClick(billingCycleChoice);
        Thread.sleep(8000);
        
        WebElement country = driver
            .findElement(By.id("parentTab:formId:childTab:trCountrySelectId_label"));
        forceClick(country);
        Thread.sleep(8000);
        
        WebElement countryChoice = driver
            .findElement(By.id("parentTab:formId:childTab:trCountrySelectId_1"));
        forceClick(countryChoice);
        Thread.sleep(8000);
        
        WebElement language = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:trLanguageSelectId_label"));
        forceClick(language);
        Thread.sleep(8000);
        
        WebElement languageChoice = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:trLanguageSelectId_2"));
        forceClick(languageChoice);
        Thread.sleep(8000);
        
        WebElement saveBtn = driver.findElements(By.className("ui-button-text-icon-left")).get(0);
        forceClick(saveBtn);
        
    }
    
    public void userAccount(WebDriver driver) throws InterruptedException {
        
        WebElement userAccount = driver.findElement(By.xpath("menu:userAccounts"));
        moveMouse(userAccount);
        Thread.sleep(8000);
        WebElement newBtn = driver.findElement(By.id("searchForm:buttonNew"));
        forceClick(newBtn);
        Thread.sleep(8000);
        
        WebElement billingAccount = driver.findElement(
            By.id("parentTab:userAccountFormId:userAccountTab:billingAccountSelectId_selectLink"));
        forceClick(billingAccount);
        Thread.sleep(8000);
        
        WebElement element = driver.findElement(By.linkText("ben.ohara"));
        element.click();
        Thread.sleep(8000);
        
        WebElement userAccountCode = driver
            .findElement(By.id("parentTab:userAccountFormId:userAccountTab:code_txt"));
        forceClick(userAccountCode);
        Thread.sleep(8000);
        
        WebElement saveBtn = driver.findElements(By.className("ui-button-text-icon-left")).get(0);
        forceClick(saveBtn);
    }
    
}