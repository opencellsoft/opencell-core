package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;

import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * 
 * @author Hassnaa MIFTAH
 */
public class ChannelsPage extends BasePage {
    
    /**
     * code.
     */
    @FindBy(id = "formId:code_txt")
    private WebElement codeCp;
    
    /**
     * search button.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement searchcodeCp;
    
    /**
     * desciption.
     */
    @FindBy(id = "formId:description")
    private WebElement descriptionCp;
    
    /**
     * code to search .
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchCp;
    
    /**
     * table element.
     */
    @FindBy(id = "datatable_results:0:code_id_message_link")
    private WebElement tableElement;
    
    /**
     * delete button
     */
    @FindBy(id = "formId:formButtonsCC:deletelink")
    private WebElement delete;
    
    public ChannelsPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * click on configuration -> channels.
     * 
     * @param driver
     * @throws InterruptedException
     */
    public void gotoListPage(WebDriver driver) throws InterruptedException {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);
        
        WebElement channelsMenu = driver.findElement(By.id("menu:channels"));
        moveMouseAndClick(channelsMenu);
        
    }
    
    /**
     * click on new Button.
     * 
     * @param driver webelement
     */
    public void gotoNewPage(WebDriver driver) {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
    }
    
    
    /**
     * fill the from.
     * 
     * @param driver webdriver
     * @param data map
     */
    public void fillForm(WebDriver driver, Map<String, String> data) {
        String test = "CH_" + System.currentTimeMillis();
        moveMouseAndClick(codeCp);
        codeCp.clear();
        codeCp.sendKeys(test);
        moveMouseAndClick(descriptionCp);
        descriptionCp.clear();
        descriptionCp.sendKeys(test);
        WebElement btnSave = driver.findElement(By.id("formId:formButtonsCC:saveButton"));
        moveMouseAndClick(btnSave);
        moveMouseAndClick(searchcodeCp);
        searchcodeCp.clear();
        searchcodeCp.sendKeys(test);
        
        /**
         * click on search button.
         */
        
        WebElement searchCp = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(searchCp);
        
        /**
         * click on the element searched.
         */
        moveMouseAndClick(tableElement);
        
        /**
         * click on delete button.
         */
        moveMouseAndClick(delete);
        /**
         * click on confirm button.
         */
        
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
        
    }
    
    /**
     * @return the codeCp
     */
    public WebElement getCodeCp() {
        return codeCp;
    }
    
    /**
     * @param codeCp the codeCp to set
     */
    public void setCodeCp(WebElement codeCp) {
        this.codeCp = codeCp;
    }
    
    /**
     * @return the searchcodeCp
     */
    public WebElement getSearchcodeCp() {
        return searchcodeCp;
    }
    
    /**
     * @param searchcodeCp the searchcodeCp to set
     */
    public void setSearchcodeCp(WebElement searchcodeCp) {
        this.searchcodeCp = searchcodeCp;
    }
    
    /**
     * @return the descriptionCp
     */
    public WebElement getDescriptionCp() {
        return descriptionCp;
    }
    
    /**
     * @param descriptionCp the descriptionCp to set
     */
    public void setDescriptionCp(WebElement descriptionCp) {
        this.descriptionCp = descriptionCp;
    }
    
    /**
     * @return the searchCp
     */
    public WebElement getSearchCp() {
        return searchCp;
    }
    
    /**
     * @param searchCp the searchCp to set
     */
    public void setSearchCp(WebElement searchCp) {
        this.searchCp = searchCp;
    }
    
    /**
     * @return the tableElement
     */
    public WebElement getTableElement() {
        return tableElement;
    }
    
    /**
     * @param tableElement the tableElement to set
     */
    public void setTableElement(WebElement tableElement) {
        this.tableElement = tableElement;
    }
    
    /**
     * @return the delete
     */
    public WebElement getDelete() {
        return delete;
    }
    
    /**
     * @param delete the delete to set
     */
    public void setDelete(WebElement delete) {
        this.delete = delete;
    }
    
}
