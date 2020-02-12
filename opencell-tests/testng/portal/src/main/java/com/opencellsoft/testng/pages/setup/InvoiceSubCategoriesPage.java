package com.opencellsoft.testng.pages.setup;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import org.openqa.selenium.support.FindBy;

/**
 * 
 * @author Maria AIT BRAHIM.
 */
public class InvoiceSubCategoriesPage extends BasePage {
    
    /**
     * invoice category choosen.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div[3]/div/div/div/div")
    private WebElement invCatChoice;
    
    /**
     * invoice category dropdown list.
     */
    @FindBy(xpath = "/html/body/div[3]/div[2]/ul/li[1]")
    private WebElement invoiceCat;
    
    /**
     * code
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div[1]/div/div/input")
    private WebElement invoiceSubCatCode;
    
    /**
     * description.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div[2]/div/div/input")
    private WebElement descriptionInvSub;
    
    /**
     * constructor.
     * 
     * @param driver
     */
    public InvoiceSubCategoriesPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * go to the configuration -> invoice sub categories.
     * 
     * @param driver web driver
     */
    
    public void gotoListPage(WebDriver driver) {
        WebElement setupMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/a[2]"));
        moveMouse(setupMenu);
        
        WebElement billingMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/div[1]/span[2]"));
        moveMouse(billingMenu);
        
        WebElement invSubCatMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/div[2]/div/div/a[5]"));
        moveMouseAndClick(invSubCatMenu);
    }
    
    /**
     * method to click on new button.
     * 
     * @param driver web driver.
     */
    public void gotoNewPage(WebDriver driver) {
        WebElement btnNew = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span"));
        
        forceClick(btnNew);
    }
    
    public void saveInvCat(WebDriver driver) {
        WebElement saveBttnInvCat = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button"));
        
        forceClick(saveBttnInvCat);
    }
    
    /**
     * fill Form method.
     * 
     * @param drive,data
     */
    
    public void fillFormCreate(WebDriver driver, Map<String, String> data) {
        
        /**
         * fill the code of the sub category.
         */
        
        moveMouseAndClick(invoiceSubCatCode);
        invoiceSubCatCode.clear();
        invoiceSubCatCode.sendKeys((String) data.get(Constants.CODE));
        /**
         * select a sub category from the drop down list.
         */
        invCatChoice.click();
        forceClick(invoiceCat);
        
    }
    
    /**
     * Searching and deleting sub categories.
     * 
     * @param driver,data
     */
    public void searchandelete(WebDriver driver, Map<String, String> data) {
        
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
    
    /**
     * @return the invSubCatChoice
     */
    public WebElement getInvCatChoice() {
        return invCatChoice;
    }
    
    /**
     * @param invSubCatChoice the invSubCatChoice to set
     */
    public void setInvSubCatChoice(WebElement invCatChoice) {
        this.invCatChoice = invCatChoice;
    }
    
    /**
     * @return the invoiceSubCat
     */
    public WebElement getInvoiceCat() {
        return invoiceCat;
    }
    
    /**
     * @param invoiceSubCat the invoiceSubCat to set
     */
    public void setInvoiceCat(WebElement invoiceSubCat) {
        this.invoiceCat = invoiceSubCat;
    }
    
    /**
     * @return the invoiceSubCatCode
     */
    public WebElement getInvoiceSubCatCode() {
        return invoiceSubCatCode;
    }
    
    /**
     * @param invoiceSubCatCode the invoiceSubCatCode to set
     */
    public void setInvoiceSubCatCode(WebElement invoiceSubCatCode) {
        this.invoiceSubCatCode = invoiceSubCatCode;
    }
    
    /**
     * @return the descriptionInvSub
     */
    public WebElement getDescriptionInvSub() {
        return descriptionInvSub;
    }
    
    /**
     * @param descriptionInvSub the descriptionInvSub to set
     */
    public void setDescriptionInvSub(WebElement descriptionInvSub) {
        this.descriptionInvSub = descriptionInvSub;
    }
    
}