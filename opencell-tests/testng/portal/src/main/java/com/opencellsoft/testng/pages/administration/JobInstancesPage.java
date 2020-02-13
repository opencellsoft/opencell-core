package com.opencellsoft.testng.pages.administration;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * 
 * @author Maria AIT BRAHIM
 *
 */
public class JobInstancesPage extends BasePage {
    /**
     * new button.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span")
    private WebElement newBtn;
    /**
     * job Category dropDown list.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div/div[3]/div/div/div")
    private WebElement jobCategory;
    /**
     * job Category choosen from the the list.
     */
    @FindBy(xpath = "/html/body/div[3]/div[2]/ul/li[2]")
    private WebElement jobCategoryChoice;
    
    /**
     * job Type choosen from the list.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div/div[4]/div/input")
    private WebElement jobTemplate;
    /**
     * code.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div/div[1]/div/input")
    private WebElement code;
    /**
     * description.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div/div[2]/div/input")
    private WebElement description;
    /**
     * timer dropDown list.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[2]/div/div/div[1]/div/div/div")
    private WebElement timer;
    /**
     * timer choosen from the list.
     */
    @FindBy(xpath = "/html/body/div[3]/div[2]/ul/li[2]")
    private WebElement timerChoice;
    /**
     * parametres tab .
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[2]/span[1]/span/span")
    private WebElement parametresTab;
    /**
     * parametres .
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[2]/div/div/div[2]/div/input")
    private WebElement parametres;
    
    /**
     * code To Search.
     */
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[1]/div[1]/div[1]/div/input")
    private WebElement codeToSearch;
    
    /**
     * element To Delete.
     */
    @FindBy(id = "#datatable_timersBean_results_data > tr:nth-child(1) > td:nth-child(2) > a")
    private WebElement elementToDelete;
    
    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public JobInstancesPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * go to menu Administration -> job instances .
     * 
     * @param driver WebDriver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement adminstrationMenu = driver.findElement(By
            .xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[5]/span[2]"));
        moveMouse(adminstrationMenu);
        WebElement jobsMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[6]/div/div/div[1]/span[2]"));
        moveMouseAndClick(jobsMenu);
        WebElement jobInstancesMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[6]/div/div/div[2]/div/div/a"));
        moveMouseAndClick(jobInstancesMenu);
    }
    
    /**
     * fill form method .
     * 
     * @param driver WebDriver
     * @throws InterruptedException
     */
    public void fillForm(WebDriver driver, Map<String, String> data) throws InterruptedException {
        
        newBtn.click();
        
        jobCategory.click();
        
        forceClick(jobCategoryChoice);
        
        moveMouseAndClick(code);
        code.sendKeys((String) data.get(Constants.CODE));
        
        moveMouseAndClick(description);
        description.sendKeys((String) data.get(Constants.CODE));
        jobTemplate.click();
        jobTemplate.clear();
        jobTemplate.sendKeys("InvoicingJob");
        forceClick(parametresTab);
        parametres.click();
        parametres.clear();
        parametres.sendKeys("parametres");
        forceClick(timer);
        
        forceClick(timerChoice);
        
        WebElement btnSave = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button/span[1]"));
        
        forceClick(btnSave);
        
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
     * @return the newBtn
     */
    public WebElement getNewBtn() {
        return newBtn;
    }
    
    /**
     * @param newBtn the newBtn to set
     */
    public void setNewBtn(WebElement newBtn) {
        this.newBtn = newBtn;
    }
    
    /**
     * @return the jobCategory
     */
    public WebElement getJobCategory() {
        return jobCategory;
    }
    
    /**
     * @param jobCategory the jobCategory to set
     */
    public void setJobCategory(WebElement jobCategory) {
        this.jobCategory = jobCategory;
    }
    
    /**
     * @return the jobCategoryChoice
     */
    public WebElement getJobCategoryChoice() {
        return jobCategoryChoice;
    }
    
    /**
     * @param jobCategoryChoice the jobCategoryChoice to set
     */
    public void setJobCategoryChoice(WebElement jobCategoryChoice) {
        this.jobCategoryChoice = jobCategoryChoice;
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
    
    /**
     * @return the timer
     */
    public WebElement getTimer() {
        return timer;
    }
    
    /**
     * @param timer the timer to set
     */
    public void setTimer(WebElement timer) {
        this.timer = timer;
    }
    
    /**
     * @return the timerChoice
     */
    public WebElement getTimerChoice() {
        return timerChoice;
    }
    
    /**
     * @param timerChoice the timerChoice to set
     */
    public void setTimerChoice(WebElement timerChoice) {
        this.timerChoice = timerChoice;
    }
    
    /**
     * @return the parametres
     */
    public WebElement getParametres() {
        return parametres;
    }
    
    /**
     * @param parametres the parametres to set
     */
    public void setParametres(WebElement parametres) {
        this.parametres = parametres;
    }
    
}
