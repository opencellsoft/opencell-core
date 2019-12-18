package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Hassnaa Miftah
 */

public class InvoiceTypesPage extends BasePage {
    @FindBy(id = "formInvoiceType:code_txt")
    private WebElement code;
    
    @FindBy(xpath = "/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement accOperation;
    
    @FindBy(id = "formInvoiceType:invoiceSequenceSelectedId_selectLink")
    private WebElement addInvSeq;
    
    @FindBy(xpath = "/html/body/div[11]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement invSeq;
    
    @FindBy(id = "searchForm:buttonNew")
    private WebElement buttonNew;
    
    @FindBy(id = "formInvoiceType:formButtonsCC:saveButton")
    private WebElement butonSave;
    
    @FindBy(id = "formInvoiceType:occTemplateSelectedId_selectLink")
    private WebElement addAccOperation;
    
    @FindBy(id = "searchForm:code_txt")
    private WebElement searchFormCode;
    
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement buttonSearch;
    
    /**
     * 
     * @param driver instance of WebDriver
     */
    public InvoiceTypesPage(final WebDriver driver) {
        super(driver);
        
    }
    
    /////////////////////
    
    /**
     * @return the code
     */
    public WebElement getCode() {
        return code;
    }
    
    /**
     * @param code the code to set
     */
    public void setCode(WebElement code) {
        this.code = code;
    }
    
    /**
     * @return the addAccOperation
     */
    public WebElement getAddAccOperation() {
        return addAccOperation;
    }
    
    /**
     * @param addAccOperation the addAccOperation to set
     */
    public void setAddAccOperation(WebElement addAccOperation) {
        this.addAccOperation = addAccOperation;
    }
    
    /**
     * @return the accOperation
     */
    public WebElement getAccOperation() {
        return accOperation;
    }
    
    /**
     * @param accOperation the accOperation to set
     */
    public void setAccOperation(WebElement accOperation) {
        this.accOperation = accOperation;
    }
    
    /**
     * @return the addInvSeq
     */
    public WebElement getAddInvSeq() {
        return addInvSeq;
    }
    
    /**
     * @param addInvSeq the addInvSeq to set
     */
    public void setAddInvSeq(WebElement addInvSeq) {
        this.addInvSeq = addInvSeq;
    }
    
    /**
     * @return the invSeq
     */
    public WebElement getInvSeq() {
        return invSeq;
    }
    
    /**
     * @param invSeq the invSeq to set
     */
    public void setInvSeq(WebElement invSeq) {
        this.invSeq = invSeq;
    }
    
    /**
     * @return the buttonNew
     */
    public WebElement getButtonNew() {
        return buttonNew;
    }
    
    /**
     * @param buttonNew the buttonNew to set
     */
    public void setButtonNew(WebElement buttonNew) {
        this.buttonNew = buttonNew;
    }
    
    /**
     * @return the butonSave
     */
    public WebElement getButonSave() {
        return butonSave;
    }
    
    /**
     * @param butonSave the butonSave to set
     */
    public void setButonSave(WebElement butonSave) {
        this.butonSave = butonSave;
    }
    
    /**
     * @return the searchFormCode
     */
    public WebElement getSearchFormCode() {
        return searchFormCode;
    }
    
    /**
     * @param searchFormCode the searchFormCode to set
     */
    public void setSearchFormCode(WebElement searchFormCode) {
        this.searchFormCode = searchFormCode;
    }
    
    /**
     * @return the buttonSearch
     */
    public WebElement getButtonSearch() {
        return buttonSearch;
    }
    
    /**
     * @param buttonSearch the buttonSearch to set
     */
    public void setButtonSearch(WebElement buttonSearch) {
        this.buttonSearch = buttonSearch;
    }
    
    /**
     * click on configuration -> billing -> invoice Types.
     * 
     * @param driver WebDriver
     */
    
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);
        
        WebElement invoiceMenu = driver.findElement(By.id("menu:invoicingconfig"));
        moveMouse(invoiceMenu);
        
        WebElement billingCyclesMenu = driver.findElement(By.id("menu:billingCycles"));
        moveMouse(billingCyclesMenu);
        
        WebElement occSubMenu = driver.findElement(By.id("menu:invoiceTypes"));
        moveMouseAndClick(occSubMenu);
        
    }
    
    /**
     * click on new invoice Type.
     * 
     * @param driver WebDriver
     */
    
    public void gotoNewPage(WebDriver driver) {
        moveMouseAndClick(buttonNew);
    }
    
    /**
     * fill the new invoice Type.
     * 
     * @param driver instance of WebDriver
     * @param data of mandatory fields
     * @throws InterruptedException
     */
    public void fillFormCreate(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(addAccOperation);
        moveMouseAndClick(accOperation);
        moveMouseAndClick(addInvSeq);
        moveMouseAndClick(invSeq);
        moveMouseAndClick(butonSave);
        moveMouseAndClick(searchFormCode);
        moveMouseAndClick(searchFormCode);
        searchFormCode.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(buttonSearch);
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
        WebElement deleteBtn = driver
            .findElement(By.id("formInvoiceType:formButtonsCC:deletelink"));
        moveMouseAndClick(deleteBtn);     
        WebElement confirmBtn = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmBtn);
        
    }
}