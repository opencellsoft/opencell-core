package com.opencellsoft.testng.pages.administration;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class EmailNotificationsPage extends BasePage {
    
    /**
     * emailNotificationFormCode.
     */
    @FindBy(id = "emailNotificationFormId:tabView:code_txt")
    private WebElement emailNotificationFormCode;
    
    
    /**
     * emailNotificationFilterList.
     */
    @FindBy(id = "emailNotificationFormId:tabView:eventTypeFilter_label")
    private WebElement emailNotificationFilterList;
    
    /**
     * emailNotificationFilter.
     */
    @FindBy(id = "emailNotificationFormId:tabView:eventTypeFilter_1")
    private WebElement emailNotificationFilter;
    /**
     * emailSentFrom.
     */
    @FindBy(id = "emailNotificationFormId:tabView:emailFrom_txt")
    private WebElement emailSentFrom;
    
    /**
     * emailSubject.
     */
    @FindBy(id = "emailNotificationFormId:tabView:emailTemplate_subject_txt")
    private WebElement emailSubject;
    /**
     * textBody.
     */
    @FindBy(id = "emailNotificationFormId:tabView:emailTemplate_textContent_txt")
    private WebElement textBody;
    
    
    /**
     * page.
     */
    @FindBy(id = "emailNotificationFormId:formButtonsCC:saveButton")
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
    @FindBy(id = "datatable_results:0:resultsdeletelink")
    private WebElement datatableDelete;
    
    /**
     * emailNotificationClassNameFilter.
     */
    @FindBy(id = "emailNotificationFormId:tabView:classNameFilter_txt_input")
    private WebElement emailNotificationClassNameFilter;

    public EmailNotificationsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * click on administration -> Filters.
     * 
     * @param driver WebDriver
     * 
     */
    public void gotoListPage(final WebDriver driver) {
        
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement adminstrationMenu = driver
                    .findElement(By.id("menu:automation"));
                moveMouseAndClick(adminstrationMenu);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        WebElement notificationsMenu = driver.findElement(By.id("menu:notifications"));
        moveMouseAndClick(notificationsMenu);
        
        WebElement emailNotificationsMenu = driver.findElement(By.id("menu:emailNotifications"));
        moveMouseAndClick(emailNotificationsMenu);
        
    }
    
    /**
     * click on emailNotifications Menu.
     * 
     * @param driver WebDriver
     */
    
    public void gotoNewPage(WebDriver driver, Map<String, String> data) {
        WebElement buttonNew = driver
                .findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(buttonNew);
        moveMouseAndClick(emailNotificationFormCode);
        emailNotificationFormCode.clear();
        emailNotificationFormCode.sendKeys((String) data.get(Constants.CODE)); 
        moveMouseAndClick(emailNotificationClassNameFilter);
        emailNotificationClassNameFilter.sendKeys("org.meveo.model.billing.BillingAccount");
        WebElement emailNotificationClassNameFilter1 = driver.findElement(By.xpath("/html/body/div[11]/ul/li"));
        moveMouseAndClick(emailNotificationClassNameFilter1);
        moveMouseAndClick(emailNotificationFilterList);
        moveMouseAndClick(emailNotificationFilter);
        moveMouseAndClick(emailSentFrom);
        emailSentFrom.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(emailSubject);
        emailSubject.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(textBody);
        textBody.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(saveButton);
        moveMouseAndClick(searchCode);
        searchCode.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(buttonSearch);
        moveMouseAndClick(datatableDelete);
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
    }
    /**
     * @return the emailNotificationFormCode
     */
    public WebElement getEmailNotificationFormCode() {
        return emailNotificationFormCode;
    }

    /**
     * @param emailNotificationFormCode the emailNotificationFormCode to set
     */
    public void setEmailNotificationFormCode(WebElement emailNotificationFormCode) {
        this.emailNotificationFormCode = emailNotificationFormCode;
    }

    /**
     * @return the emailNotificationFilterList
     */
    public WebElement getEmailNotificationFilterList() {
        return emailNotificationFilterList;
    }

    /**
     * @param emailNotificationFilterList the emailNotificationFilterList to set
     */
    public void setEmailNotificationFilterList(WebElement emailNotificationFilterList) {
        this.emailNotificationFilterList = emailNotificationFilterList;
    }

    /**
     * @return the emailNotificationFilter
     */
    public WebElement getEmailNotificationFilter() {
        return emailNotificationFilter;
    }

    /**
     * @param emailNotificationFilter the emailNotificationFilter to set
     */
    public void setEmailNotificationFilter(WebElement emailNotificationFilter) {
        this.emailNotificationFilter = emailNotificationFilter;
    }

    /**
     * @return the emailSentFrom
     */
    public WebElement getEmailSentFrom() {
        return emailSentFrom;
    }

    /**
     * @param emailSentFrom the emailSentFrom to set
     */
    public void setEmailSentFrom(WebElement emailSentFrom) {
        this.emailSentFrom = emailSentFrom;
    }

    /**
     * @return the emailSubject
     */
    public WebElement getEmailSubject() {
        return emailSubject;
    }

    /**
     * @param emailSubject the emailSubject to set
     */
    public void setEmailSubject(WebElement emailSubject) {
        this.emailSubject = emailSubject;
    }

    /**
     * @return the textBody
     */
    public WebElement getTextBody() {
        return textBody;
    }

    /**
     * @param textBody the textBody to set
     */
    public void setTextBody(WebElement textBody) {
        this.textBody = textBody;
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

    /**
     * @return the emailNotificationClassNameFilter
     */
    public WebElement getEmailNotificationClassNameFilter() {
        return emailNotificationClassNameFilter;
    }

    /**
     * @param emailNotificationClassNameFilter the emailNotificationClassNameFilter to set
     */
    public void setEmailNotificationClassNameFilter(WebElement emailNotificationClassNameFilter) {
        this.emailNotificationClassNameFilter = emailNotificationClassNameFilter;
    }
}
