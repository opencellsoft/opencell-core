package com.opencellsoft.testng.pages.customers;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class UserAccountPage extends BasePage {
    
    @FindBy(id = "searchForm:buttonNew")
    private WebElement buttonNew;
    
    @FindBy(id = "parentTab:userAccountFormId:userAccountTab:billingAccountSelectId_selectLink")
    private WebElement billingAccountSelect;
    
    @FindBy(xpath = "/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td")
    private WebElement billingAccount;
    
    @FindBy(id = "parentTab:userAccountFormId:userAccountTab:code_txt")
    private WebElement userAccountCode;
    
    @FindBy(id = "parentTab:userAccountFormId:formButtonsCC:saveButtonAjax")
    private WebElement saveButton;
    
    @FindBy(id = "parentTab:userAccountFormId:formButtonsCC:backButton")
    private WebElement backButton;
    
    @FindBy(id = "searchForm:code_txt")
    private WebElement searchCode;
    
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement buttonSearch;

    
    public UserAccountPage(WebDriver driver) {
        super(driver);
    }
    
    public void gotoListPage(WebDriver driver) {
        WebElement customersMenu = driver.findElement(By.id("menu:crm"));
        moveMouse(customersMenu);
        
        WebElement userAccounts = driver.findElement(By.id("menu:userAccounts"));
        moveMouseAndClick(userAccounts);
        
    }
    
    public void fillFormUserAccount(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(buttonNew);
        billingAccountSelect.click();
        moveMouseAndClick(billingAccount);
        moveMouseAndClick(userAccountCode);
        userAccountCode.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(saveButton);
        moveMouseAndClick(backButton);
        moveMouseAndClick(searchCode);
        searchCode.clear();
        searchCode.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(buttonSearch);
        
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement resultsDeleteLink = driver
                    .findElement(By.id("datatable_results:0:resultsdeletelink"));
                moveMouseAndClick(resultsDeleteLink);
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
