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
public class ChartOfAccountsPage extends BasePage {
    
    /**
     * menuFinance Menu.
     */
    @FindBy(id = "menu:finance")
    private WebElement menuFinance;
    /**
     * chartOfAccounts Page.
     */
    @FindBy(id = "menu:chartOfAccounts")
    private WebElement chartOfAccounts;
    
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
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div[4]/div/div/div[3]/span")
    private WebElement typeLists;
    
    /**
     * chartsOfAccountType.
     */
    @FindBy(id = "formId:chartOfAccountTypeEnum_enum_1")
    private WebElement chartsOfAccountType;
    
    /**
     * chartsOfAccountViewList.
     */
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div[6]/div/div/div[3]/span")
    private WebElement chartsOfAccountViewList;
    
    /**
     * chartsOfAccountViewList.
     */
    @FindBy(id = "formId:chartOfAccountViewTypeEnum_enum_1")
    private WebElement chartsOfAccountView;
    
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
    
    public ChartOfAccountsPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }
    
    public void opendDrBuilderList() throws InterruptedException {
        moveMouseAndClick(menuFinance);
        moveMouseAndClick(chartOfAccounts);
    }
    
    public void  fillChartOfAccountsAndSave(final WebDriver driver,
            Map<String, String> data) throws InterruptedException {
        moveMouseAndClick(buttonNew);
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(typeLists);
        moveMouseAndClick(chartsOfAccountType);
        moveMouseAndClick(chartsOfAccountViewList);
        moveMouseAndClick(chartsOfAccountView);
        moveMouseAndClick(saveButton);
        
    }
    
    public void searchChartOfAccountsAndDelete(final WebDriver driver,
            Map<String, String> data) throws InterruptedException {
        moveMouseAndClick(codeToSearch);
        codeToSearch.clear();
        codeToSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(searchBtn);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement chartToDelete = driver
                    .findElement(By.id("datatable_results:0:resultsdeletelink"));
                moveMouseAndClick(chartToDelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
      //  waitUntilElementDisplayed(deleteBtn, driver);
      //  forceClick(deleteBtn);
        
        
        /**
         * click on confirm button.
         */
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
        
    }
    
}
