package com.opencellsoft.testng.pages.setup;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Currencies page.
 * 
 * @author MAria AIT BRAHIM
 * 
 *
 */
public class Currencies extends BasePage {
    
    /**
     * button new.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement btnNew;
    /**
     * button reset.
     */
    @FindBy(id = "currenciesFormId:formButtonsCC:resetButtonCC:resetButton")
    private WebElement btnReset;
    /**
     * button save.
     */
    @FindBy(id = "currenciesFormId:formButtonsCC:saveButton")
    private WebElement btnSave;
    
    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public Currencies(final WebDriver driver) {
        super(driver);
    }
    
    /**
     * Entering data.
     * 
     * @param driver web driver
     * @throws InterruptedException
     */
    public void fillCurrencies(WebDriver driver) throws InterruptedException {
        WebElement btnNew = driver.findElement((By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span")));
        
        btnNew.click();
        WebElement description = driver.findElement((By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div/div[2]/div/input")));
        
        forceClick(description);
        description.sendKeys("descriptionL");
        
        WebElement languageselect = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div/div[1]/div/input"));
        
        forceClick(languageselect);
        languageselect.sendKeys("LLN");
        WebElement btnSave = driver.findElement((By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button/span[1]")));
        
        forceClick(btnSave);
        
    }
    
    /**
     * Opening driver.
     * 
     * @param driver web driverF
     */
    public void gotoListPage(WebDriver driver) {
        WebElement setupMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/a[2]"));
        moveMouse(setupMenu);
        
        WebElement currencyMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/a[11]"));
        moveMouseAndClick(currencyMenu);
        
    }
    
    /**
     * Searching and deleting sub categories.
     * 
     * @param driver,data
     */
    public void searchandelete(WebDriver driver) {
        
        WebElement codeToSearch = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[1]/div[1]/div[1]/div/input"));
        
        forceClick(codeToSearch);
        codeToSearch.clear();
        codeToSearch.sendKeys("LLN");
        
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
    
}
