package com.opencellsoft.testng.pages.setup;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * @author Maria AIT BRAHIM.
 *
 */
public class DiscountPlanPage extends BasePage {
    
    /**
     * code.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div/div[1]/div/input")
    private WebElement code;
    
    /**
     * description.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div/div[2]/div/input")
    private WebElement description;
    
    /**
     * constructor
     * 
     * @param driver
     */
    public DiscountPlanPage(WebDriver driver) {
        super(driver);
        
    }
    
    /**
     * go to setup > Discount Plan
     * 
     * @param driver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement setupMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/a[2]"));
        moveMouse(setupMenu);
        
        WebElement discountPlanMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/a[15]"));
        moveMouseAndClick(discountPlanMenu);
    }
    
    /**
     * go to new page.
     * 
     * @param driver WebDriver
     */
    
    public void goNewPage(WebDriver driver) {
        WebElement btnNew = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span"));
        
        btnNew.click();
    }
    
    public void fillFormCreate(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        
        code.click();
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        
        description.click();
        description.clear();
        description.sendKeys((String) data.get(Constants.DESCRIPTION));
        
    }
    
    /**
     * the button save.
     * 
     * @param driver WebDriver
     */
    
    public void saveOperation(WebDriver driver) {
        WebElement btnSave = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button/span[1]"));
        
        btnSave.click();
        
    }
    
    /**
     * Searching and deleting discount plan .
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
     * @return the description
     */
    public WebElement getDescription() {
        return description;
    }
    
    /**
     * @param description the description to set
     */
    public void setDescription(WebElement description) {
        this.description = description;
    }
    
}
