package com.opencellsoft.testng.pages.entities;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * 
 * @author Miftah
 *
 */
public class EventPage extends BasePage {
    
    public EventPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * menuReporting Menu.
     */
    @FindBy(id = "menu:customEntites")
    private WebElement customEntites;
    /**
     * menu entities Page.
     */
    @FindBy(id = "menu:cet_1")
    private WebElement entitiesPage;
    
    /**
     * buttonNew.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement buttonNew;
    
    /**
     * code.
     */
    @FindBy(id = "formId:tabView:code_txt")
    private WebElement code;
    
    /**
     * save Btn.
     */
    @FindBy(id = "formId:formButtonsCC:saveButtonAjax")
    private WebElement saveBtn;
    
    /**
     * event Tab.
     */
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/ul/li[2]/a")
    private WebElement eventTab;
    
    public void  openEventList(WebDriver driver) throws InterruptedException {
        moveMouse(customEntites);
        moveMouseAndClick(entitiesPage);
    }
    
    public void createNewEvent(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        buttonNew.click();
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(eventTab);
        WebElement eventdate = driver.findElements(By.className("ui-inputfield")).get(2);
        moveMouseAndClick(eventdate);
        eventdate.sendKeys("25/01/2019");
        WebElement eventList = driver.findElements(By.className("ui-selectonemenu-label")).get(0);
        moveMouseAndClick(eventList);
        WebElement event = driver.findElements(By.className("ui-selectonemenu-list-item")).get(3);
        moveMouseAndClick(event);
        WebElement createdBy = driver.findElements(By.className("ui-state-default")).get(8);
        moveMouseAndClick(createdBy);
        createdBy.clear();
        createdBy.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(saveBtn);
        
    }
    
    public void searchEventDelete(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        
        WebElement codeToDelete = driver.findElement(By.id("searchForm:code_txt"));
        moveMouseAndClick(codeToDelete);
        codeToDelete.clear();
        codeToDelete.sendKeys((String) data.get(Constants.CODE));
        WebElement buttonSearch = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(buttonSearch);
        WebElement chartToDelete = driver
            .findElement(By.id("datatable_results:0:code_id_message_link"));
        moveMouseAndClick(chartToDelete);
        WebElement deleteBtn = driver.findElement(By.id("formId:formButtonsCC:deletelink"));
        moveMouseAndClick(deleteBtn);
        /**
         * click on confirm button.
         */
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
        
    }

    /**
     * @return the customEntites
     */
    public WebElement getCustomEntites() {
        return customEntites;
    }

    /**
     * @param customEntites the customEntites to set
     */
    public void setCustomEntites(WebElement customEntites) {
        this.customEntites = customEntites;
    }

    /**
     * @return the entitiesPage
     */
    public WebElement getEntitiesPage() {
        return entitiesPage;
    }

    /**
     * @param entitiesPage the entitiesPage to set
     */
    public void setEntitiesPage(WebElement entitiesPage) {
        this.entitiesPage = entitiesPage;
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
     * @return the eventTab
     */
    public WebElement getEventTab() {
        return eventTab;
    }

    /**
     * @param eventTab the eventTab to set
     */
    public void setEventTab(WebElement eventTab) {
        this.eventTab = eventTab;
    }
    
}
