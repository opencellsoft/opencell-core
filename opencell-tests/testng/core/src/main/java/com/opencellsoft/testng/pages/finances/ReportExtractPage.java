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

public class ReportExtractPage extends BasePage {
    /**
     * menuFinance Menu.
     */
    @FindBy(id = "menu:finance")
    private WebElement menuFinance;
    /**
     * chartOfAccounts Page.
     */
    @FindBy(id = "menu:reportExtracts")
    private WebElement reportExtract;

    /**
     * scriptTypeList.
     */
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/table/tbody/tr/td[2]/div/div/div[3]/div/div/div[3]/span")
    private WebElement scriptTypeList;
    /**
     * scriptType.
     */
    @FindBy(id = "reportExtractForm:scriptType_enum_1")
    private WebElement scriptType;
    
    /**
     * scriptInstanceList.
     */
    @FindBy(id = "reportExtractForm:scriptInstanceSelectId_selectLink")
    private WebElement scriptInstanceList;
    /**
     * scriptInstance.
     */
    @FindBy(xpath = "/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[2]")
    private WebElement scriptInstance;
    
    /**
     * fileFormat.
     */
    @FindBy(id = "reportExtractForm:filenameFormat_txt")
    private WebElement fileFormat;
    
    /**
     * saveBtn.
     */
    @FindBy(id = "reportExtractForm:formButtonsCC:saveButton")
    private WebElement saveBtn;
    
    /**
     * codeToSearch.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeToSearch;
    
    /**
     * codeToSearch.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement buttonSearch;
    
    /**
     * chartToDelete.
     */
    @FindBy(id = "reportExtractForm:formButtonsCC:deletelink")
    private WebElement deleteBtn;
    
    public ReportExtractPage(WebDriver driver) {
        super(driver);
    }
    
    public void openReportExtractList(final WebDriver driver) throws InterruptedException {
        moveMouseAndClick(menuFinance);
        moveMouseAndClick(reportExtract);
        
    }
    
    public void fillReportExtractAndSave(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement buttonNew = driver.findElement(By.xpath("/html/body/div[2]/form[2]/div/div/div/div[3]/button[3]/span"));
                moveMouseAndClick(buttonNew);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement code = driver.findElement(By.id("reportExtractForm:code_txt"));
                moveMouseAndClick(code);
                code.clear();
                code.sendKeys((String) data.get(Constants.CODE));
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
       
        moveMouseAndClick(scriptTypeList);
        moveMouseAndClick(scriptType);
        moveMouseAndClick(scriptInstanceList);
        moveMouseAndClick(scriptInstance);
        moveMouseAndClick(fileFormat);
        fileFormat.sendKeys((".csv"));
        moveMouseAndClick(saveBtn);
        
    }
    
    public void searchReportExtractPageAndDelete(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(codeToSearch);
        codeToSearch.clear();
        codeToSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(buttonSearch);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement reportExtractToDelete = driver
                    .findElement(By.id("datatable_results:0:resultsdeletelink"));
                moveMouseAndClick(reportExtractToDelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
     
        /**
         * click on confirm button.
         */
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
        
    }
    
}
