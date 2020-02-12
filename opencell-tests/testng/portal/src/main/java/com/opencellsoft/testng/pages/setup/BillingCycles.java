package com.opencellsoft.testng.pages.setup;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Billing Cycles page.
 * 
 * @author Maria AIT BRAHIM.
 * 
 * 
 *
 */
public class BillingCycles extends BasePage {
    /**
     * button new.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span")
    private WebElement btnNew;
    /**
     * new code label.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[1]/div/div/input")
    private WebElement codeBilling;
    /**
     * new description label.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[2]/div/div/input")
    private WebElement descriptionBilling;
    /**
     * Invoices Tab.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[2]/span[1]/span")
    private WebElement invoicesTab;
   
    /**
     * invoicing Threshold.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[2]/div/div[3]/div/div/input")
    private WebElement invoicingThreshold;
   
    /**
     * delai date facture.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[2]/div/div[1]/div/div/input")
    private WebElement delaiDateFacture;
    
    /**
     * button to choose from calender.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[3]/div/div/div/div")
    private WebElement calenderChoose;
    /**
     * calendar.
     */
    @FindBy(xpath = "/html/body/div[3]/div[2]/ul/li[8]")
    private WebElement calendar;
    
    /**
     * due day delay.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[2]/div/div[5]/div/div/input")
    private WebElement dueDateDelay;
    /**
     * button save.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button[1]/span[1]")
    private WebElement btnSave;
   
    
    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public BillingCycles(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Opening billing cycle page.
     * 
     * @param driver billing cycle
     */
    public void gotoListPage(WebDriver driver) {
        
        WebElement setupMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/a[2]"));
        moveMouse(setupMenu);
        
        WebElement billingMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/div[1]/span[2]"));
        moveMouse(billingMenu);
        
        WebElement billingCycles = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/div[2]/div/div/a[1]"));
        moveMouseAndClick(billingCycles);
    }
    
    /**
     * clicking on new.
     * 
     * @param driver WebDriver
     */
    public void goTobtnNew(WebDriver driver) {
       
        btnNew.click();
    }
    
    /**
     * Entering data.
     * 
     * @param driver new billing cycle
     * @param data code,description,billing template,
     * @throws InterruptedException
     */
    public void fillData(WebDriver driver, Map<String, String> data) throws InterruptedException {
        
        codeBilling.click();
        codeBilling.clear();
        codeBilling.sendKeys((String) data.get(Constants.CODE));
        
        descriptionBilling.click();
        descriptionBilling.clear();
        descriptionBilling.sendKeys((String) data.get(Constants.DESCRIPTION));
        forceClick(calenderChoose);
        forceClick(calendar);
        forceClick(invoicesTab);
        
        delaiDateFacture.click();
        delaiDateFacture.sendKeys("1");
        
        invoicingThreshold.click();
        invoicingThreshold.clear();
        invoicingThreshold.sendKeys("1");
        
        dueDateDelay.click();
        dueDateDelay.sendKeys("1");
        
       
    }
    
    /**
     * clicking on save.
     * 
     * @param driver WebDriver
     */
    public void goToSave(WebDriver driver) {
      
        forceClick(btnSave);
    }
    
    /**
     * Entering and searching data.
     * 
     * @param driver Web driver
     * @param data data to search for
     */
    public void fillAndSearche(WebDriver driver, Map<String, String> data) {
       
        
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

    public WebElement getCodeBilling() {
        return codeBilling;
    }

    public void setCodeBilling(WebElement codeBilling) {
        this.codeBilling = codeBilling;
    }

    public WebElement getDescriptionBilling() {
        return descriptionBilling;
    }

    public void setDescriptionBilling(WebElement descriptionBilling) {
        this.descriptionBilling = descriptionBilling;
    }

    public WebElement getInvoicingThreshold() {
        return invoicingThreshold;
    }

    public void setInvoicingThreshold(WebElement invoicingThreshold) {
        this.invoicingThreshold = invoicingThreshold;
    }

    public WebElement getDelaiDateFacture() {
        return delaiDateFacture;
    }

    public void setDelaiDateFacture(WebElement delaiDateFacture) {
        this.delaiDateFacture = delaiDateFacture;
    }

    public WebElement getCalenderChoose() {
        return calenderChoose;
    }

    public void setCalenderChoose(WebElement calenderChoose) {
        this.calenderChoose = calenderChoose;
    }

    public WebElement getCalendar() {
        return calendar;
    }

    public void setCalendar(WebElement calendar) {
        this.calendar = calendar;
    }

    public WebElement getDueDateDelay() {
        return dueDateDelay;
    }

    public void setDueDateDelay(WebElement dueDateDelay) {
        this.dueDateDelay = dueDateDelay;
    }
    
   
    
}
