package com.opencellsoft.testng.pages.payments;

import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class DDrBuildersPage extends BasePage {
    
    public DDrBuildersPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * payment Menu.
     */
    @FindBy(id = "menu:payment")
    private WebElement paymentMenu;
    /**
     * directDebit Page.
     */
    @FindBy(id = "menu:directDebit")
    private WebElement directDebitPage;
    
    /**
     * dDRequestBuilders Page.
     */
    @FindBy(id = "menu:dDRequestBuilders")
    private WebElement dDRequestBuilders;
    
    /**
     * buttonNew .
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement buttonNew;
    
    @FindBy(id = "formDDRequestBuilder:tabView:code_txt")
    private WebElement code;
    
    @FindBy(id = "formDDRequestBuilder:tabView:scriptSelectId_selectLink")
    private WebElement scriptList;
    
    @FindBy(xpath = "/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement script;
    
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/div/div/div[6]/div/div/div[3]/span")
    private WebElement paymentLevelList;
    
    @FindBy(id = "formDDRequestBuilder:tabView:paymentLevel_enum_1")
    private WebElement paymentLevel;
    
    @FindBy(id = "formDDRequestBuilder:formButtonsCC:saveButton")
    private WebElement save;
    
    @FindBy(id = "formDDRequestBuilder:formButtonsCC:backButton")
    private WebElement backBtn;
    
    public void opendDrBuilderList(final WebDriver driver) throws InterruptedException {
        moveMouseAndClick(paymentMenu);
        moveMouseAndClick(directDebitPage);
        moveMouseAndClick(paymentMenu);
        moveMouseAndClick(dDRequestBuilders);
        
    }
    
    public void fillDDrBuildersAndSave(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(buttonNew);        
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(scriptList);
        moveMouseAndClick(script);        
        moveMouseAndClick(paymentLevelList);
        moveMouseAndClick(paymentLevel);
        moveMouseAndClick(save);
        moveMouseAndClick(backBtn);   
    }
    
    public void searchDDrBuildersAndDelete(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        // Code to Search
        WebElement searchFormCode = driver.findElement(By.id("searchForm:code_txt"));
        moveMouseAndClick(searchFormCode);
        searchFormCode.clear();
        searchFormCode.sendKeys((String) data.get(Constants.CODE));
        // Click on search Btn
        WebElement searchBtn = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(searchBtn);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement rowTODelete = driver
                    .findElement(By.id("datatable_results:0:resultsdeletelink"));
                moveMouseAndClick(rowTODelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
      /*  WebElement deleteBtn = driver
            .findElement(By.id("formDDRequestBuilder:formButtonsCC:deletelink"));
        waitUntilElementDisplayed(deleteBtn, driver);
        forceClick(deleteBtn);
        */
        
        /**
         * click on confirm button.
         */
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
        
    }
    
}
