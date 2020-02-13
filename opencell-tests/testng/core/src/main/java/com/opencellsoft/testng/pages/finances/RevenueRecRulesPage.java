package com.opencellsoft.testng.pages.finances;

import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * 
 * @author Miftah
 *
 */
public class RevenueRecRulesPage extends BasePage {
    
    /**
     * menuFinance Menu.
     */
    @FindBy(id = "menu:finance")
    private WebElement menuFinance;
    /**
     * chartOfAccounts Page.
     */
    @FindBy(id = "menu:revenueRecognitionRules")
    private WebElement revenueRecRules;
    
    /**
     * buttonNew.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement buttonNew;
    
    /**
     * code.
     */
    @FindBy(id = "formId:code_txt")
    private WebElement code;
    
    /**
     * typeLists.
     */
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/fieldset[1]/div/div[3]/div/div/div[3]/span")
    private WebElement revenueRecRulesScriptList;
    
    /**
     * chartsOfAccountType.
     */
    @FindBy(id = "formId:rrScriptId_3")
    private WebElement revenueRecRulesScript;
    
    /**
     * saveButton.
     */
    @FindBy(id = "formId:formButtonsCC:saveButton")
    private WebElement saveButton;
    
    /**
     * codeToSearch.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeToSearch;
    
    /**
     * searchBtn.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBtn;

    /**
     * chartToDelete.
     */
    @FindBy(id = "formId:formButtonsCC:deletelink")
    private WebElement deleteBtn;
    
    public RevenueRecRulesPage(WebDriver driver) {
        super(driver);
    }
    
    public void openRevenueRecRulesList(final WebDriver driver) throws InterruptedException {
        moveMouseAndClick(menuFinance);
        moveMouseAndClick(revenueRecRules);
        
    }
    
    public void fillRevenueRecRulesAndSave(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(buttonNew);
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(revenueRecRulesScriptList);
        moveMouseAndClick(revenueRecRulesScript);
        moveMouseAndClick(saveButton);
        
    }
    
    public void searchRevenueRecRulesPageAndDelete(final WebDriver driver,
            Map<String, String> data) throws InterruptedException {
        moveMouseAndClick(codeToSearch);
        codeToSearch.clear();
        codeToSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(searchBtn);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement chartToDelete = driver
                    .findElement(By.id("datatable_results:0:code_id_message_link"));
                moveMouseAndClick(chartToDelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        moveMouseAndClick(deleteBtn);
        
        
        /**
         * click on confirm button.
         */
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
    }
    
}
