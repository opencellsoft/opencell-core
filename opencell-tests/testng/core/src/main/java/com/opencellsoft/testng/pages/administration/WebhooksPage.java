package com.opencellsoft.testng.pages.administration;

import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class WebhooksPage extends BasePage {
    
    /**
     * buttonNew.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement buttonNew;
    
    /**
     * webHookCode.
     */
    @FindBy(id = "webHookFormId:code_txt")
    private WebElement webHookCode;
    
    /**
     * className Filter List.
     */
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div[2]/div/span/button")
    private WebElement classNameFilterList;
    
    /**
     * classNameFilter.
     */
    @FindBy(xpath = "/html/body/div[11]/ul/li[5]")
    private WebElement classNameFilter;
    /**
     * eventTypeList.
     */
    @FindBy(id = "webHookFormId:eventTypeFilter_label")
    private WebElement eventTypeList;
    
    /**
     * eventTypeList.
     */
    @FindBy(id = "webHookFormId:eventTypeFilter_2")
    private WebElement eventType;
    
    /**
     * httpMethodList.
     */
    @FindBy(id = "webHookFormId:httpMethod_enum_label")
    private WebElement httpMethodList;
    
    /**
     * httpMethod.
     */
    @FindBy(id = "webHookFormId:httpMethod_enum_3")
    private WebElement httpMethod;
    
    /**
     * host.
     */
    @FindBy(id = "webHookFormId:host_txt")
    private WebElement host;
    
    /**
     * page.
     */
    @FindBy(id = "webHookFormId:page_txt")
    private WebElement page;
    
    /**
     * page.
     */
    @FindBy(id = "webHookFormId:formButtonsCC:saveButton")
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
    
    public WebhooksPage(WebDriver driver) {
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
        
        WebElement webhooksMenu = driver.findElement(By.id("menu:webHooks"));
        moveMouseAndClick(webhooksMenu);
        
    }
    
    /**
     * click on administration Menu.
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
    
    /**
     * fill the new filter.
     * 
     * @param driver instance of WebDriver
     * @param data Map
     */
    public void fillFormCreate(WebDriver driver, Map<String, String> data) {
        // Code
        moveMouseAndClick(webHookCode);
        webHookCode.clear();
        webHookCode.sendKeys((String) data.get(Constants.CODE));
        WebElement classNameFilteInput = driver
            .findElement(By.id("webHookFormId:classNameFilter_txt_input"));
        moveMouseAndClick(classNameFilteInput);
        classNameFilteInput.sendKeys("org.meveo.model.billing.InvoiceCategory");
        WebElement classNameFilteInput1 = driver.findElement(By.xpath("/html/body/div[11]/ul/li"));
        moveMouseAndClick(classNameFilteInput1);
        moveMouseAndClick(eventTypeList);
        moveMouseAndClick(eventType);
        moveMouseAndClick(httpMethodList);
        moveMouseAndClick(httpMethod);
        moveMouseAndClick(host);
        host.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(page);
        page.sendKeys((String) data.get(Constants.CODE));
        
    }
    
    public void searchWebHookAndDelete(WebDriver driver, Map<String, String> data) {
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
     * @return the webHookCode
     */
    public WebElement getWebHookCode() {
        return webHookCode;
    }
    
    /**
     * @param webHookCode the webHookCode to set
     */
    public void setWebHookCode(WebElement webHookCode) {
        this.webHookCode = webHookCode;
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
     * @return the eventTypeList
     */
    public WebElement getEventTypeList() {
        return eventTypeList;
    }
    
    /**
     * @param eventTypeList the eventTypeList to set
     */
    public void setEventTypeList(WebElement eventTypeList) {
        this.eventTypeList = eventTypeList;
    }
    
    /**
     * @return the eventType
     */
    public WebElement getEventType() {
        return eventType;
    }
    
    /**
     * @param eventType the eventType to set
     */
    public void setEventType(WebElement eventType) {
        this.eventType = eventType;
    }
    
    /**
     * @return the httpMethodList
     */
    public WebElement getHttpMethodList() {
        return httpMethodList;
    }
    
    /**
     * @param httpMethodList the httpMethodList to set
     */
    public void setHttpMethodList(WebElement httpMethodList) {
        this.httpMethodList = httpMethodList;
    }
    
    /**
     * @return the httpMethod
     */
    public WebElement getHttpMethod() {
        return httpMethod;
    }
    
    /**
     * @param httpMethod the httpMethod to set
     */
    public void setHttpMethod(WebElement httpMethod) {
        this.httpMethod = httpMethod;
    }
    
    /**
     * @return the host
     */
    public WebElement getHost() {
        return host;
    }
    
    /**
     * @param host the host to set
     */
    public void setHost(WebElement host) {
        this.host = host;
    }
    
    /**
     * @return the page
     */
    public WebElement getPage() {
        return page;
    }
    
    /**
     * @param page the page to set
     */
    public void setPage(WebElement page) {
        this.page = page;
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
