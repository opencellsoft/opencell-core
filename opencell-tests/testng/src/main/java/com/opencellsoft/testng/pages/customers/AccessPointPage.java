package com.opencellsoft.testng.pages.customers;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class AccessPointPage extends BasePage {
    
    @FindBy(id = "searchForm:buttonNew")
    private WebElement buttonNew;
    
    @FindBy(id = "formId:tabView:accessUserId_txt")
    private WebElement accessUserId;
    
    @FindBy(id = "formId:tabView:subscriptionId_selectLink")
    private WebElement selectSubscription;
    
    @FindBy(xpath = "/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement subscription;
    
    @FindBy(id = "formId:formButtonsCC:saveButtonAjax")
    private WebElement saveBtn;
    
    @FindBy(id = "searchForm:accessUserId_txt")
    private WebElement codeToSearch;
    
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement buttonSearch;
    
    @FindBy(id = "datatable_results:0:accessUserId_id_message_link")
    private WebElement pointToDelete;
    
    public AccessPointPage(WebDriver driver) {
        super(driver);
    }
    
    public void gotoListPage(WebDriver driver) {
        WebElement customersMenu = driver.findElement(By.id("menu:crm"));
        moveMouse(customersMenu);
        
        WebElement subbscription = driver.findElement(By.id("menu:accessPoints"));
        moveMouseAndClick(subbscription);
    }
    
    public void fillFormAccessPoint(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(buttonNew);
        moveMouseAndClick(accessUserId);
        accessUserId.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(selectSubscription);
        moveMouseAndClick(subscription);
        moveMouseAndClick(saveBtn);
        moveMouseAndClick(codeToSearch);
        codeToSearch.clear();
        codeToSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(buttonSearch);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement buttonDelete = driver
                    .findElement(By.id("datatable_results:0:resultsdeletelink"));
                moveMouseAndClick(buttonDelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
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
     * @return the accessUserId
     */
    public WebElement getAccessUserId() {
        return accessUserId;
    }
    
    /**
     * @param accessUserId the accessUserId to set
     */
    public void setAccessUserId(WebElement accessUserId) {
        this.accessUserId = accessUserId;
    }
    
    /**
     * @return the selectSubscription
     */
    public WebElement getSelectSubscription() {
        return selectSubscription;
    }
    
    /**
     * @param selectSubscription the selectSubscription to set
     */
    public void setSelectSubscription(WebElement selectSubscription) {
        this.selectSubscription = selectSubscription;
    }
    
    /**
     * @return the subscription
     */
    public WebElement getSubscription() {
        return subscription;
    }
    
    /**
     * @param subscription the subscription to set
     */
    public void setSubscription(WebElement subscription) {
        this.subscription = subscription;
    }
    
    /**
     * @return the saveBtn
     */
    public WebElement getSaveBtn() {
        return saveBtn;
    }
    
    /**
     * @param saveBtn the saveBtn to set
     */
    public void setSaveBtn(WebElement saveBtn) {
        this.saveBtn = saveBtn;
    }
    
    /**
     * @return the codeToSearch
     */
    public WebElement getCodeToSearch() {
        return codeToSearch;
    }
    
    /**
     * @param codeToSearch the codeToSearch to set
     */
    public void setCodeToSearch(WebElement codeToSearch) {
        this.codeToSearch = codeToSearch;
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
     * @return the pointToDelete
     */
    public WebElement getPointToDelete() {
        return pointToDelete;
    }
    
    /**
     * @param pointToDelete the pointToDelete to set
     */
    public void setPointToDelete(WebElement pointToDelete) {
        this.pointToDelete = pointToDelete;
    }
   
}
