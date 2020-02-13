package com.opencellsoft.testng.pages.customers;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * customer Account.
 * 
 * @author AIT BRAHIM Maria
 *
 */
public class CustomerAccountPage extends BasePage {
    
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[2]/div[1]/div/input")
    private WebElement description;
    
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[5]/div[2]/div/div/div")
    private WebElement currency;
    
    @FindBy(xpath = "/html/body/div[3]/div[2]/ul/li")
    private WebElement currencyItem;
    
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[6]/div/div/div/div")
    private WebElement language;
    
    @FindBy(xpath = "/html/body/div[3]/div[2]/ul/li[1]")
    private WebElement languageItem;
    
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[1]/div[1]/div/div/input")
    private WebElement customer;
    
    public CustomerAccountPage(WebDriver driver) {
        super(driver);
    }
    
    public void gotoListPage(WebDriver driver) {
        WebElement customersMenu = driver.findElement(By
            .xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[1]/span[2]"));
        moveMouse(customersMenu);
        
        WebElement customersAccountMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[2]/div/div/a[3]"));
        moveMouseAndClick(customersAccountMenu);
        
    }
    
    public void fillFormCustomerAccount(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        WebElement btnNew = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span"));
        
        btnNew.click();
        
        customer.click();
        
        WebElement customerItem = driver
            .findElement(By.xpath("/html/body/div[3]/div/ul/li[1]/div/div/strong"));
        
        forceClick(customerItem);
        WebElement code = driver.findElement((By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[1]/div[2]/div/input")));
        
        moveMouseAndClick(code);
        code.sendKeys((String) data.get(Constants.CODE));
        waitUntilElementDisplayed(description, driver);
        description.click();
        description.clear();
        description.sendKeys("this is a description");
        currency.click();
        
        currencyItem.click();
        
        language.click();
        
        languageItem.click();
        
        WebElement information = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[3]/span[1]/span/span"));
        
        forceClick(information);
        WebElement email = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div[1]/div/div/input"));
        
        email.click();
        email.clear();
        email.sendKeys("opencell@opencell.com");
        
        WebElement adressTab = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[4]/span[1]/span/span"));
        
        forceClick(adressTab);
        
        WebElement adress = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[4]/div/div[1]/div/div/input"));
        
        adress.click();
        adress.clear();
        adress.sendKeys("Paris-france");
        
        WebElement codePostal = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[4]/div/div[4]/div/div/input"));
        
        codePostal.click();
        codePostal.clear();
        codePostal.sendKeys("2344");
        
        WebElement city = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[4]/div/div[5]/div/div/input"));
        
        city.click();
        city.clear();
        city.sendKeys("Paris");
        
        WebElement country = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[4]/div/div[6]/div/div/input"));
        
        country.click();
        country.clear();
        country.sendKeys("France");
        
        WebElement btnSave = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button/span[1]"));
        
        forceClick(btnSave);
        WebElement codeToSearch = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[1]/div[1]/div[1]/div/input"));
        
        forceClick(codeToSearch);
        codeToSearch.clear();
        codeToSearch.sendKeys((String) data.get(Constants.CODE));
        
        WebElement btnCheck = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr/td[1]/span/span[1]/input"));
        
        forceClick(btnCheck);
        
        WebElement elementToDelete = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[1]/div[2]/button/span[1]/span"));
        
        forceClick(elementToDelete);
        
        WebElement confirmDelete = driver
            .findElement(By.xpath("/html/body/div[3]/div[2]/div[3]/button[2]/span[1]"));
        
        confirmDelete.click();
        
    }
    
    public WebElement getDescription() {
        return description;
    }
    
    public void setDescription(WebElement description) {
        this.description = description;
    }
    
    public WebElement getCurrency() {
        return currency;
    }
    
    public void setCurrency(WebElement currency) {
        this.currency = currency;
    }
    
    public WebElement getCurrencyItem() {
        return currencyItem;
    }
    
    public void setCurrencyItem(WebElement currencyItem) {
        this.currencyItem = currencyItem;
    }
    
    public WebElement getLanguage() {
        return language;
    }
    
    public void setLanguage(WebElement language) {
        this.language = language;
    }
    
    public WebElement getLanguageItem() {
        return languageItem;
    }
    
    public void setLanguageItem(WebElement languageItem) {
        this.languageItem = languageItem;
    }
    
    public WebElement getCustomer() {
        return customer;
    }
    
    public void setCustomer(WebElement customer) {
        this.customer = customer;
    }
    
}