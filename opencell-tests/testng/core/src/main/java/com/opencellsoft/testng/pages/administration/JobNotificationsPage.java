package com.opencellsoft.testng.pages.administration;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class JobNotificationsPage extends BasePage {

    /**
     * buttonNew.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement buttonNew;
    
    /**
     * jobNotificationCode
     */
    @FindBy(id = "jobTriggerForm:code_txt")
    private WebElement jobNotificationCode;
    
    /**
     * classNameFilterList
     */
    @FindBy(id = "jobTriggerForm:classNameFilter_txt_input")
    private WebElement classNameFilterList;
    
    /**
     * classNameFilter
     */
    @FindBy(id = "jobTriggerForm:eventTypeFilter_2")
    private WebElement classNameFilter;
    
    /**
     * jobInstanceList
     */
    @FindBy(id = "jobTriggerForm:jobInstanceSelectId_selectLink")
    private WebElement jobInstanceList;
    
    /**
     * jobInstance
     */
    @FindBy(xpath = "/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement jobInstance;
    
    /**
     * eventTypeList
     */
    @FindBy(id = "jobTriggerForm:eventTypeFilter_label")
    private WebElement eventTypeList;
    
    /**
     * eventTypeList
     */
    @FindBy(id = "jobTriggerForm:eventTypeFilter_1")
    private WebElement eventType;
    /**
     * jobInstance
     */
    @FindBy(id = "jobTriggerForm:formButtonsCC:saveButton")
    private WebElement saveButton;
    /**
     * searchCode.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement searchCode;
    
    /**
     * buttonSearch.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement buttonSearch;
    
    /**
     * datatableDelete.
     */
    @FindBy(id = "datatable_jobTriggers:0:jobTriggersdeletelink")
    private WebElement datatableDelete;
    
    public JobNotificationsPage(WebDriver driver) {
        super(driver);
    }
    /**
     * click on administration -> Filters.
     * 
     * @param driver WebDriver
     * 
     */
    public void gotoListPage(final WebDriver driver) {
        
        WebElement administrationMenu = driver.findElement(By.id("menu:automation"));
        moveMouse(administrationMenu);
        
        WebElement notificationsMenu = driver.findElement(By.id("menu:notifications"));
        moveMouseAndClick(notificationsMenu);
        
        WebElement emailNotificationsMenu = driver.findElement(By.id("menu:jobTriggers"));
        moveMouseAndClick(emailNotificationsMenu);
        
    }
    /**
     * click on emailNotifications Menu.
     * 
     * @param driver WebDriver
     */
    
    public void gotoNewPage(WebDriver driver) {
        moveMouseAndClick(buttonNew);
    }

    
    /**
     * the button save.
     * 
     * @param driver WebDriver
     */
    public void save(WebDriver driver) {
        moveMouseAndClick(saveButton);
    }
    public void fillFormCreate(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(jobNotificationCode);
        jobNotificationCode.clear();
        jobNotificationCode.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(classNameFilterList);
        classNameFilterList.sendKeys("org.meveo.model.billing.BillingAccount");
        WebElement classNameFilterList1 = driver.findElement(By.xpath("/html/body/div[11]/ul/li"));
        moveMouseAndClick(classNameFilterList1);
        moveMouseAndClick(eventTypeList);
        moveMouseAndClick(eventType);
        moveMouseAndClick(jobInstanceList);
        moveMouseAndClick(jobInstance);
    }
    public void searchNotifAndDelete(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(searchCode);
        searchCode.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(buttonSearch);
        moveMouseAndClick(datatableDelete);
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
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
     * @return the jobNotificationCode
     */
    public WebElement getJobNotificationCode() {
        return jobNotificationCode;
    }
    /**
     * @param jobNotificationCode the jobNotificationCode to set
     */
    public void setJobNotificationCode(WebElement jobNotificationCode) {
        this.jobNotificationCode = jobNotificationCode;
    }
    /**
     * @return the classNameFilterList
     */
    public WebElement getClassNameFilterList() {
        return classNameFilterList;
    }
    /**
     * @param classNameFilterList the classNameFilterList to set
     */
    public void setClassNameFilterList(WebElement classNameFilterList) {
        this.classNameFilterList = classNameFilterList;
    }
    /**
     * @return the classNameFilter
     */
    public WebElement getClassNameFilter() {
        return classNameFilter;
    }
    /**
     * @param classNameFilter the classNameFilter to set
     */
    public void setClassNameFilter(WebElement classNameFilter) {
        this.classNameFilter = classNameFilter;
    }
    /**
     * @return the jobInstanceList
     */
    public WebElement getJobInstanceList() {
        return jobInstanceList;
    }
    /**
     * @param jobInstanceList the jobInstanceList to set
     */
    public void setJobInstanceList(WebElement jobInstanceList) {
        this.jobInstanceList = jobInstanceList;
    }
    /**
     * @return the jobInstance
     */
    public WebElement getJobInstance() {
        return jobInstance;
    }
    /**
     * @param jobInstance the jobInstance to set
     */
    public void setJobInstance(WebElement jobInstance) {
        this.jobInstance = jobInstance;
    }
    /**
     * @return the saveButton
     */
    public WebElement getSaveButton() {
        return saveButton;
    }
    /**
     * @param saveButton the saveButton to set
     */
    public void setSaveButton(WebElement saveButton) {
        this.saveButton = saveButton;
    }
    /**
     * @return the searchCode
     */
    public WebElement getSearchCode() {
        return searchCode;
    }
    /**
     * @param searchCode the searchCode to set
     */
    public void setSearchCode(WebElement searchCode) {
        this.searchCode = searchCode;
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
     * @return the datatableDelete
     */
    public WebElement getDatatableDelete() {
        return datatableDelete;
    }
    /**
     * @param datatableDelete the datatableDelete to set
     */
    public void setDatatableDelete(WebElement datatableDelete) {
        this.datatableDelete = datatableDelete;
    }
}
