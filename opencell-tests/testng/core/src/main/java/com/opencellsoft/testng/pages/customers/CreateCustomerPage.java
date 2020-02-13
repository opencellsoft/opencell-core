package com.opencellsoft.testng.pages.customers;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class CreateCustomerPage extends BasePage{

    public CreateCustomerPage(WebDriver driver) {
        super(driver);
    }
    /**
     * customer menu.
     */
    @FindBy(id = "menu:crm")
    private WebElement customerMenu;
    
    /**
     * customer page.
     */
    @FindBy(id = "menu:customers")
    private WebElement customerPage;
    /**
     * add new customer menu.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement addNewCustomerMenu;
    
    /**
     * customer description field.
     */
    @FindBy(id = "formCustomer:tabView:description")
    private WebElement descriptionIpt;
    
    /**
     * customer's code field.
     */
    @FindBy(id = "formCustomer:tabView:code")
    private WebElement code;
    
    /**
     * customer category drop down list .
     */
    @FindBy(id = "formCustomer:tabView:customerCategory_entity_label")
    private WebElement customerCategorylist;
    
    /**
     * add customer category .
     */
    @FindBy(id = "formCustomer:tabView:customerCategory_entity_1")
    private WebElement customerCategory;
    
    /**
     * save button.
     */
    @FindBy(id = "formCustomer:formButtonsCC:saveButton")
    private WebElement saveBtn;
    
    public void  fillCustomerAndSave( WebDriver driver,Map<String, String> data) throws InterruptedException {
        waitUntilElementDisplayed(addNewCustomerMenu, driver);
       forceClick(addNewCustomerMenu);
       waitUntilElementDisplayed(descriptionIpt, driver);
       descriptionIpt.click();
       descriptionIpt.clear();
       descriptionIpt.sendKeys((String) data.get(Constants.DESCRIPTION));
       waitUntilElementDisplayed(code, driver);
       code.click();
       code.clear();
       code.sendKeys((String) data.get(Constants.CODE));
       waitUntilElementDisplayed(customerCategorylist, driver);
       forceClick(customerCategorylist);
              
       WebElement customerCategory = driver.findElement(By.id("formCustomer:tabView:customerCategory_entity_1"));
       waitUntilElementDisplayed(customerCategory, driver);
       forceClick(customerCategory);
       
       WebElement saveBtn = driver.findElement(By.id("formCustomer:formButtonsCC:saveButton"));
       waitUntilElementDisplayed(saveBtn, driver);
       forceClick(saveBtn);
    }
}
