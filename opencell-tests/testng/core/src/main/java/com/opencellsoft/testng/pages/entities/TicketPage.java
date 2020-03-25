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
public class TicketPage extends BasePage {

    /**
     * menuReporting Menu.
     */
    @FindBy(id = "menu:customEntites")
    private WebElement customEntites;
    /**
     * menu entities Page.
     */
    @FindBy(id = "menu:cet_0")
    private WebElement ticketsPage;
    
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
     * ticket Tab.
     */
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/ul/li[2]/a")
    private WebElement ticketTab;

    public TicketPage(WebDriver driver) {
        super(driver);
        }
    public void openTicketList() throws InterruptedException {
        moveMouse(customEntites);
        moveMouseAndClick(ticketsPage);
        
    }
    public void createNewEvent(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        buttonNew.click();
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(ticketTab);
        WebElement statusDate = driver.findElements(By.className("ui-state-default")).get(7);
        moveMouseAndClick(statusDate);
        statusDate.sendKeys("25/01/2019");
        WebElement creationDate = driver.findElements(By.className("ui-state-default")).get(10);
        moveMouseAndClick(creationDate);
        creationDate.sendKeys("25/01/2019");
        WebElement createdBy = driver.findElements(By.className("ui-state-default")).get(11);
        moveMouseAndClick(createdBy);
        createdBy.sendKeys("test test ");
        WebElement uAccountList = driver.findElement(By.className(
              "ui-corner-left"));
        moveMouseAndClick(uAccountList);
        uAccountList.sendKeys("john.doe");
        WebElement customerUserAccount = driver.findElement(By.xpath("/html/body/div[10]/table/tbody/tr/td[1]"));
        moveMouseAndClick(customerUserAccount);
        WebElement handeledBy = driver.findElements(By.className("ui-state-default")).get(18);
        moveMouseAndClick(handeledBy);
        handeledBy.clear();
        handeledBy.sendKeys("test test ");
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
     * @return the ticketsPage
     */
    public WebElement getTicketsPage() {
        return ticketsPage;
    }
    /**
     * @param ticketsPage the ticketsPage to set
     */
    public void setTicketsPage(WebElement ticketsPage) {
        this.ticketsPage = ticketsPage;
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
     * @return the ticketTab
     */
    public WebElement getTicketTab() {
        return ticketTab;
    }
    /**
     * @param ticketTab the ticketTab to set
     */
    public void setTicketTab(WebElement ticketTab) {
        this.ticketTab = ticketTab;
    }
}
