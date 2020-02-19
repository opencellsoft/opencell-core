package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * CatalogRecurringPage.
 * 
 * @author Hassnaa MIFTAH
 */
public class CatalogRecurringPage extends BasePage {
    
    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public CatalogRecurringPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * go to catalog -> reccuring menu .
     * 
     * @param driver WebDriver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement catalogMenu = driver.findElement(By.id("menu:catalog"));
        moveMouse(catalogMenu);
        WebElement serviceManagement = driver.findElement(By.id("menu:serviceManagement"));
        moveMouse(serviceManagement);
        WebElement chargesMenu = driver.findElement(By.id("menu:charges"));
        moveMouse(chargesMenu);
        for (int i = 0; i < 2; i++) {
            try
            {
                WebElement recurringMenu = driver
                    .findElement(By.id("menu:recurringChargeTemplate"));
                moveMouseAndClick(recurringMenu);
                break;
            }
            
            catch (StaleElementReferenceException see)
            {
            }
        }
    }
    
    /**
     * fill form and save method .
     * 
     * @param driver WebDriver
     * @param data Map
     */
    public void fillFormAndSave(WebDriver driver, Map<String, String> data) {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
        WebElement code = driver.findElement(By.id("reccuringChargeId:tabView:code_txt"));
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        WebElement agreementExtension = driver
            .findElement(By.id("reccuringChargeId:tabView:durationTermInMonth_number_input"));
        moveMouseAndClick(agreementExtension);
        agreementExtension.clear();
        agreementExtension.sendKeys("16");
        WebElement LevelOfChargeSharingItems = driver
            .findElement(By.id("reccuringChargeId:tabView:shareLevel_enum_label"));
        LevelOfChargeSharingItems.click();
        WebElement levelOfChargeSharing = driver
            .findElement(By.id("reccuringChargeId:tabView:shareLevel_enum_1"));
        levelOfChargeSharing.click();
        WebElement inputUnitDescirption = driver
            .findElement(By.id("reccuringChargeId:tabView:inputUnitDescription_txt"));
        moveMouseAndClick(inputUnitDescirption);
        inputUnitDescirption.clear();
        inputUnitDescirption.sendKeys("12");
        WebElement ratingUnitDescription = driver
            .findElement(By.id("reccuringChargeId:tabView:ratingUnitDescription_txt"));
        moveMouseAndClick(ratingUnitDescription);
        ratingUnitDescription.clear();
        ratingUnitDescription.sendKeys("4");
        WebElement unitMultiplicator = driver
            .findElement(By.id("reccuringChargeId:tabView:unitMultiplicator_number"));
        moveMouseAndClick(unitMultiplicator);
        unitMultiplicator.clear();
        unitMultiplicator.sendKeys("2");
        WebElement unitNbDecimal = driver
            .findElement(By.id("reccuringChargeId:tabView:unitNbDecimal_number_input"));
        moveMouseAndClick(unitNbDecimal);
        unitNbDecimal.clear();
        unitNbDecimal.sendKeys("4");
        WebElement roundingModeItems = driver
            .findElement(By.id("reccuringChargeId:tabView:roundingMode_enum_label"));
        moveMouseAndClick(roundingModeItems);
        WebElement roundingMode = driver
            .findElement(By.id("reccuringChargeId:tabView:roundingMode_enum_1"));
        roundingMode.click();
        WebElement filterExpression = driver
            .findElement(By.id("reccuringChargeId:tabView:filterExpression_txt"));
        filterExpression.click();
        filterExpression.clear();
        filterExpression.sendKeys("filter expression");
        WebElement description = driver.findElement(By.id("reccuringChargeId:tabView:description"));
        description.click();
        description.clear();
        description.sendKeys("this is description ");
        WebElement subcategoryItems = driver
            .findElement(By.id("reccuringChargeId:tabView:invoiceSubCategorySelectedId_label"));
        subcategoryItems.click();
        WebElement subcategory = driver
            .findElement(By.id("reccuringChargeId:tabView:invoiceSubCategorySelectedId_1"));
        subcategory.click();
        WebElement calendarItems = driver
            .findElement(By.id("reccuringChargeId:tabView:calendar_entity_label"));
        calendarItems.click();
        WebElement calendar = driver
            .findElement(By.id("reccuringChargeId:tabView:calendar_entity_1"));
        moveMouseAndClick(calendar);
        WebElement button = driver
            .findElement(By.cssSelector("button.ui-button-icon-only:nth-child(1)"));
        button.click();
        WebElement saveBtn = driver
            .findElement(By.id("reccuringChargeId:formButtonsCC:saveButtonAjax"));
        moveMouseAndClick(saveBtn);
        WebElement backBtn = driver
            .findElement(By.id("reccuringChargeId:formButtonsCC:backButton"));
        moveMouseAndClick(backBtn);
        WebElement codeSearch = driver.findElement(By.id("searchForm:code_txt"));
        moveMouseAndClick(codeSearch);
        codeSearch.clear();
        codeSearch.sendKeys((String) data.get(Constants.CODE));
        WebElement searchBtn = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(searchBtn);
        for (int i = 0; i < 5; i++) {
            try
            
            {
                WebElement rowTODelete = driver
                    .findElement(By.id("datatable_results:0:code_id_message_link"));
                moveMouseAndClick(rowTODelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }

        WebElement deletebttn = driver
            .findElement(By.id("reccuringChargeId:formButtonsCC:deletelink"));
        moveMouseAndClick(deletebttn);
        
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
        
    }
}