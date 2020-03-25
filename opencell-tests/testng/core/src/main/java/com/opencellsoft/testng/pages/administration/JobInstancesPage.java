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
 * @author HASSNAA MIFTAH
 *
 */
public class JobInstancesPage extends BasePage {

    /**
     * job Category dropDown list.
     */
    @FindBy(id = "formId:tabView:jobCategoryEnum_enum_label")
    private WebElement jobCategory;
    /**
     * job Category choosen from the the list.
     */
    @FindBy(id = "formId:tabView:jobCategoryEnum_enum_7")
    private WebElement jobCategoryChoice;
    /**
     * job Type dropDown list. formId:tabView:timerJobName_label
     */
    @FindBy(id = "formId:tabView:timerJobName_label")
    private WebElement jobType;
    /**
     * job Type choosen from the list.
     */
    @FindBy(id = "formId:tabView:timerJobName_6")
    private WebElement jobTypeChoice;
    /**
     * code.
     */
    @FindBy(id = "formId:tabView:code_txt")
    private WebElement code;
    /**
     * description.
     */
    @FindBy(id = "formId:tabView:description_txt")
    private WebElement description;
    /**
     * timer dropDown list.
     */
    @FindBy(id = "formId:tabView:timer_label")
    private WebElement timer;
    /**
     * timer choosen from the list.
     */
    @FindBy(id = "formId:tabView:timer_1")
    private WebElement timerChoice;
    /**
     * parametres.
     */
    @FindBy(id = "formId:tabView:parametres_txt")
    private WebElement parametres;
    /**
     * next job.
     */
    @FindBy(id = "formId:tabView:timerFollowingJob_label")
    private WebElement nextJob;
    /**
     * next job choosen from the list .
     */
    @FindBy(id = "formId:tabView:timerFollowingJob_1")
    private WebElement nextJobChoice;
    /**
     * run On Nodes.
     */
    @FindBy(id = "formId:tabView:runOnNodes_txt")
    private WebElement runOnNodes;
    /**
     * job Category Search.
     */
    @FindBy(id = "searchForm:jobCategory_label")
    private WebElement jobCategorySearchItem;
    /**
     * job Category Search.
     */
    @FindBy(id = "searchForm:jobCategory_5")
    private WebElement jobCategorySearch;
    /**
     * search Button.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBtn;
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
        WebElement adminstrationMenu = driver.findElement(By.id("menu:automation"));
        moveMouse(adminstrationMenu);
        WebElement jobsMenu = driver.findElement(By.id("menu:adminJobs"));
        moveMouseAndClick(jobsMenu);
        WebElement jobInstancesMenu = driver.findElement(By.id("menu:jobs"));
        moveMouseAndClick(jobInstancesMenu);
    }
    
    /**
     * fill form method .
     * 
     * @param driver WebDriver
     * @throws InterruptedException
     */
    public void fillForm(WebDriver driver, Map<String, String> data) throws InterruptedException {
        WebElement buttonNew = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(buttonNew);
        jobCategory.click();
        jobCategoryChoice.click();
        moveMouseAndClick(jobType);
        moveMouseAndClick(jobTypeChoice);
        moveMouseAndClick(code);
        code.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(description);
        description.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(timer);
        timerChoice.click();
        parametres.clear();
        parametres.sendKeys("parametres");
        nextJob.click();
        nextJobChoice.click();
        runOnNodes.click();
        runOnNodes.clear();
        runOnNodes.sendKeys("comma separated list");
        WebElement btnSave = driver.findElements(By.className("ui-button-text-icon-left")).get(0);
        moveMouseAndClick(btnSave);
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
     * @return the jobType
     */
    public WebElement getJobType() {
        return jobType;
    }
    
    /**
     * @param jobType the jobType to set
     */
    public void setJobType(WebElement jobType) {
        this.jobType = jobType;
    }
    
    /**
     * @return the jobTypeChoice
     */
    public WebElement getJobTypeChoice() {
        return jobTypeChoice;
    }
    
    /**
     * @param jobTypeChoice the jobTypeChoice to set
     */
    public void setJobTypeChoice(WebElement jobTypeChoice) {
        this.jobTypeChoice = jobTypeChoice;
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
    
    /**
     * @return the nextJob
     */
    public WebElement getNextJob() {
        return nextJob;
    }
    
    /**
     * @param nextJob the nextJob to set
     */
    public void setNextJob(WebElement nextJob) {
        this.nextJob = nextJob;
    }
    
    /**
     * @return the nextJobChoice
     */
    public WebElement getNextJobChoice() {
        return nextJobChoice;
    }
    
    /**
     * @param nextJobChoice the nextJobChoice to set
     */
    public void setNextJobChoice(WebElement nextJobChoice) {
        this.nextJobChoice = nextJobChoice;
    }
    
    /**
     * @return the runOnNodes
     */
    public WebElement getRunOnNodes() {
        return runOnNodes;
    }
    
    /**
     * @param runOnNodes the runOnNodes to set
     */
    public void setRunOnNodes(WebElement runOnNodes) {
        this.runOnNodes = runOnNodes;
    }
}
