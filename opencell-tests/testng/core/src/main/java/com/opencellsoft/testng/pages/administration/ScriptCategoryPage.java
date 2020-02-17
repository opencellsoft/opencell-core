package com.opencellsoft.testng.pages.administration;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class ScriptCategoryPage extends BasePage {
    /**
     * button new.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement btnNew;
    /**
     * new code label.
     */
    @FindBy(id = "formId:code_txt")
    private WebElement code;
    /**
     * new description label.
     */
    @FindBy(id = "formId:description_txt")
    private WebElement description;
    /**
     * button save.
     * 
     */
    @FindBy(id = "formId:formButtonsCC:saveButton")
    private WebElement btnSave;
    
    /**
     * codeto search.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement searchCode;
    
    /**
     * buttonSearch.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement buttonSearch;
    
    /**
     * button delete.
     */
    @FindBy(id = "datatable_results:0:resultsdeletelink")
    private WebElement btnDelete;
    
    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public ScriptCategoryPage(final WebDriver driver) {
        super(driver);
    }
    
    /**
     * Opening offer model menu.
     * 
     * @param driver WebDriver
     * @throws InterruptedException
     */
    public void gotoListPage(WebDriver driver) throws InterruptedException {
        
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
        WebElement scriptMenu = driver.findElement(By.id("menu:script"));
        moveMouseAndClick(scriptMenu);
        WebElement scriptCategoryMenu = driver.findElement(By.id("menu:scriptInstanceCategories"));
        moveMouseAndClick(scriptCategoryMenu);
    }
    
    /**
     * clicking on new.
     * 
     * @param driver WebDriver
     */
    public void goTobtnNew(WebDriver driver) {
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
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(description);
        description.clear();
        description.sendKeys((String) data.get(Constants.DESCRIPTION));
    }
    
    /**
     * clicking on save.
     * 
     * @param driver WebDriver
     */
    public void goToSave(WebDriver driver) {
        moveMouseAndClick(btnSave);
    }
    
    /**
     * Delete selected data.
     * 
     * @param driver web driver
     */
    public void delete(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(searchCode);
        searchCode.clear();
        searchCode.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(buttonSearch);
        moveMouseAndClick(btnDelete);
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
    }
    
    /**
     * @return the btnNew
     */
    public WebElement getBtnNew() {
        return btnNew;
    }
    
    /**
     * @param btnNew the btnNew to set
     */
    public void setBtnNew(WebElement btnNew) {
        this.btnNew = btnNew;
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
     * @return the btnSave
     */
    public WebElement getBtnSave() {
        return btnSave;
    }
    
    /**
     * @param btnSave the btnSave to set
     */
    public void setBtnSave(WebElement btnSave) {
        this.btnSave = btnSave;
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
     * @return the btnDelete
     */
    public WebElement getBtnDelete() {
        return btnDelete;
    }
    
    /**
     * @param btnDelete the btnDelete to set
     */
    public void setBtnDelete(WebElement btnDelete) {
        this.btnDelete = btnDelete;
    }
    
}
