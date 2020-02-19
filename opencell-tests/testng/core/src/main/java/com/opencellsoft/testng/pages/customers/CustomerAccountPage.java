package com.opencellsoft.testng.pages.customers;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class CustomerAccountPage extends BasePage {
    
    @FindBy(id = "parentTab:formCustomerAccount:childTab:description")
    private WebElement description;
    
    @FindBy(id = "parentTab:formCustomerAccount:childTab:externalRef1_txt")
    private WebElement refExt1;
    
    @FindBy(id = "parentTab:formCustomerAccount:childTab:primaryContact_entity_label")
    private WebElement primaryContact;
    
    @FindBy(id = "parentTab:formCustomerAccount:childTab:primaryContact_entity_1")
    private WebElement primaryContactItem;
    
    @FindBy(id = "parentTab:formCustomerAccount:childTab:currencySelectId_label")
    private WebElement currency;
    
    @FindBy(id = "parentTab:formCustomerAccount:childTab:currencySelectId_1")
    private WebElement currencyItem;
    
    @FindBy(id = "parentTab:formCustomerAccount:childTab:trLanguageSelectId_label")
    private WebElement language;
    
    @FindBy(id = "parentTab:formCustomerAccount:childTab:trLanguageSelectId_1")
    private WebElement languageItem;
    
    @FindBy(id = "parentTab:formCustomerAccount:childTab:customerSelectId_selectLink")
    private WebElement customer;
    
    public CustomerAccountPage(WebDriver driver) {
        super(driver);
    }
    
    public void gotoListPage(WebDriver driver) {
        WebElement customersMenu = driver.findElement(By.id("menu:crm"));
        moveMouse(customersMenu);
        
        WebElement customersAccountMenu = driver.findElement(By.id("menu:customerAccounts"));
        moveMouseAndClick(customersAccountMenu);
        
    }
    
    public void fillFormCustomerAccount(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
        customer.click();
        
        WebElement customerItem = driver.findElement(
            By.xpath("/html/body/div[10]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]"));
        moveMouseAndClick(customerItem);
        WebElement code =  driver.findElement((By.id("parentTab:formCustomerAccount:childTab:code_txt")));
        moveMouseAndClick(code);
        code.sendKeys((String) data.get(Constants.CODE));
        description.click();
        description.clear();
        description.sendKeys("this is a description");
        refExt1.click();
        refExt1.clear();
        refExt1.sendKeys("this is a reference");
        primaryContact.click();
        primaryContactItem.click();
        currency.click();
        currencyItem.click();
        language.click();
        languageItem.click();
        WebElement information = driver
            .findElement(By.cssSelector("li.ui-state-default:nth-child(2) > a:nth-child(1)"));
        moveMouseAndClick(information);
        WebElement email = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:email"));
        
        email.click();
        email.clear();
        email.sendKeys("opencell@opencell.com");
        
        WebElement phone = driver.findElement(
            By.id("parentTab:formCustomerAccount:childTab:contactInformation_phone_txt"));
        phone.click();
        phone.clear();
        phone.sendKeys("06060066");
        
        WebElement adress = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:address1"));
        adress.click();
        adress.clear();
        adress.sendKeys("Paris-france");
        WebElement addNew = driver.findElements(By.className("ui-button-text-only")).get(0);        
        moveMouseAndClick(addNew);
        
        WebElement alias = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:alias_txt"));
        alias.click();
        alias.clear();
        alias.sendKeys("123456852369");
        WebElement addPM = driver.findElements(By.className("ui-button-text-only")).get(1);
        moveMouseAndClick(addPM);
        WebElement btnSave = driver
            .findElement(By.id("parentTab:formCustomerAccount:formButtonsCC:saveButton"));
        moveMouseAndClick(btnSave);
        
        WebElement btnBack = driver
            .findElement(By.id("parentTab:formCustomerAccount:formButtonsCC:backButton"));
        moveMouseAndClick(btnBack);
        
        WebElement codeToSearch = driver.findElement(By.id("searchForm:code_txt"));
        codeToSearch.click();
        codeToSearch.clear();
        codeToSearch.sendKeys((String) data.get(Constants.CODE));
        WebElement searchBtn = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(searchBtn);
        
        WebElement elementToDelete = driver
            .findElement(By.id("datatable_results:0:code_id_message_link"));
        moveMouseAndClick(elementToDelete);
        
        
        
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement deleteBtn = driver
                    .findElement(By.id("parentTab:formCustomerAccount:formButtonsCC:deletelink"));
                moveMouseAndClick(deleteBtn);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
        
    }
}