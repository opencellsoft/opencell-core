package com.opencellsoft.testng.pages.administration;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * notification page.
 * 
 * @author MIFTAH
 *
 */
public class Notification extends BasePage {

    /**
     * new code label.
     */
    @FindBy(id = "notificationForm:code_txt")
    private WebElement codeNotification;
    /**
     * new entity type.
     */
    // @FindBy(id = "notificationForm:eventTypeFilter_label")
    // private WebElement entityType;
    /**
     * choose type of entity.
     */
    @FindBy(id = "notificationForm:eventTypeFilter_2")
    private WebElement chooseEntity;
    /**
     * event el filter.
     */
    @FindBy(id = "notificationForm:elFilter_txt")
    private WebElement eventFilter;
    /**
     * script instance.
     */
    @FindBy(id = "notificationForm:scriptInstanceSelectId_selectLink")
    private WebElement scriptInstance;
    /**
     * button save.
     */
    @FindBy(id = "notificationForm:formButtonsCC:saveButton")
    private WebElement btnSave;
    /**
     * code search.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeSearch;
    /**
     * button search.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement buttonSearch;
    /**
     * button delete.
     */
    @FindBy(id = "datatable_notifications:0:notificationsdeletelink")
    private WebElement btnDelete;
    
    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public Notification(final WebDriver driver) {
        super(driver);
    }
    
    /**
     * Opening offer model menu.
     * 
     * @param driver WebDriver
     */
    public void gotoListPage(WebDriver driver) {
        
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
        WebElement notifications = driver.findElement(By.id("menu:notifications"));
        moveMouseAndClick(notifications);
        WebElement notificationNotification = driver
            .findElement(By.id("menu:internalNotifications"));
        moveMouseAndClick(notificationNotification);
    }
    
    /**
     * clicking on new.
     * 
     * @param driver WebDriver
     */
    public void goTobtnNew(WebDriver driver) {
        WebElement btnNew = driver
                .findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
    }
    
    /**
     * entering data.
     * 
     * @param driver WebDriver
     * @param data code, title, description
     * @throws InterruptedException
     */
    public void fillData(WebDriver driver, Map<String, String> data) throws InterruptedException {
        moveMouseAndClick(codeNotification);
        codeNotification.clear();
        codeNotification.sendKeys((String) data.get(Constants.CODE));
        WebElement type = driver.findElements(By.className("ui-autocomplete-dropdown")).get(0);
        type.click();
        WebElement chooseType = driver.findElements(By.className("ui-autocomplete-item")).get(2);
        moveMouseAndClick(chooseType);
        WebElement entityType = driver.findElement(
            By.xpath("/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div[3]/div/div/div[3]/span"));
        moveMouseAndClick(entityType);
        moveMouseAndClick(chooseEntity);
        moveMouseAndClick(eventFilter);
        eventFilter.clear();
        eventFilter.sendKeys((String) data.get(Constants.DESCRIPTION));
        scriptInstance.click();
        moveMouseAndClick(driver.findElement(
            By.xpath("/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")));
        moveMouseAndClick(btnSave);
    }
    /**
     * Delete selected data.
     * 
     * @param driver web driver
     */
    public void delete(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(codeSearch);
        codeSearch.clear();
        codeSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(buttonSearch);
        moveMouseAndClick(btnDelete);
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
    }
    /**
     * code setter.
     * 
     * @param codeNotification setter
     */
    public void setcodeNotification(WebElement codeNotification) {
        this.codeNotification = codeNotification;
    }
    
    /**
     * code getter.
     * 
     * @return code
     */
    public WebElement getcodeWorkflow() {
        return this.codeNotification;
    }
    
    /**
     * description setter.
     * 
     * @param eventFilter setter
     */
    public void seteventFilter(WebElement eventFilter) {
        this.eventFilter = eventFilter;
    }
    
    /**
     * description getter.
     * 
     * @return description
     */
    public WebElement geteventFilter() {
        return this.eventFilter;
    }
    
}
