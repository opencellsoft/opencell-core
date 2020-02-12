package com.opencellsoft.testng.pages.setup;

import com.opencellsoft.testng.pages.BasePage;

import com.opencellsoft.testng.pages.Constants;

import java.util.Map;

import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * 
 * @author Maria AIT BRAHIM.
 */

public class InvoiceCategoriesPage extends BasePage {
    
    /**
     * code of a new invoice categories.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div/div[1]/div/input")
    private WebElement codeInvCat;
    
    /**
     * description.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div/div[2]/div/input")
    private WebElement descriptionInvCat;
    
    /**
     * constructor.
     * 
     * @param driver
     */
    public InvoiceCategoriesPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * click on configuration -> invoice categories.
     * 
     * @param driver web driver.
     */
    public void gotoListPage(WebDriver driver) {
        WebElement setupMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/a[2]"));
        moveMouse(setupMenu);
        
        WebElement billingMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/div[1]/span[2]"));
        moveMouse(billingMenu);
        
        WebElement invCatMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/div[2]/div/div/a[3]"));
        moveMouseAndClick(invCatMenu);
        
    }
    
    /**
     * go to new page.
     * 
     * @param driver
     */
    
    public void gotoNewPage(WebDriver driver) {
        WebElement btnNew = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span"));
        
        btnNew.click();
    }
    
    /**
     * fillFormCreate.
     * 
     * @param driver,data
     * @throws InterruptedException
     */
    
    public void fillFormCreate(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        /**
         * give the code of the invoice categories.
         */
        
        moveMouseAndClick(codeInvCat);
        codeInvCat.clear();
        codeInvCat.sendKeys((String) data.get(Constants.CODE));
        /**
         * give the description of the invoice categories.
         */
        
        moveMouseAndClick(descriptionInvCat);
        descriptionInvCat.clear();
        descriptionInvCat.sendKeys((String) data.get(Constants.DESCRIPTION));
        
        WebElement saveBttnInvCat = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button/span[1]"));
        
        saveBttnInvCat.click();
    }
    
    /**
     * Entering and searching data.
     * 
     * @param driver Web driver
     * @param data data to search for
     */
    public void fillAndSearch(WebDriver driver, Map<String, String> data) {
        
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
     * @return the codeInvCat
     */
    public WebElement getCodeInvCat() {
        return codeInvCat;
    }
    
    /**
     * @param codeInvCat the codeInvCat to set
     */
    public void setCodeInvCat(WebElement codeInvCat) {
        this.codeInvCat = codeInvCat;
    }
    
    /**
     * @return the descriptionInvCat
     */
    public WebElement getDescriptionInvCat() {
        return descriptionInvCat;
    }
    
    /**
     * @param descriptionInvCat the descriptionInvCat to set
     */
    public void setDescriptionInvCat(WebElement descriptionInvCat) {
        this.descriptionInvCat = descriptionInvCat;
    }
    
}
