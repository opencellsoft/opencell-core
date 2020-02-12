package com.opencellsoft.testng.pages.configuration;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;

/**
 * @author MIFTAH HASSNAA
 *
 */
public class BrandsPage extends BasePage {
    
    /**
     * code.
     */
    @FindBy(id = "formId:code_txt")
    private WebElement code;
    
    /**
     * description.
     */
    @FindBy(id = "formId:description")
    private WebElement description;
    
    /**
     * search Button
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBtn;
    
    /**
     * code To Search.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeToSearch;
    
    /**
     * element To Delete
     */
    @FindBy(id = "datatable_results:0:code_id_message_link")
    private WebElement elementToDelete;
    
    /**
     * delete button
     */
    @FindBy(id = "formId:formButtonsCC:deletelink")
    private WebElement delete;
    
    /**
     * constructor
     * 
     * @param driver
     */
    public BrandsPage(WebDriver driver) {
        super(driver);
        
    }
    
    /**
     * go to Configuration > Customers > Brands
     * 
     * @param driver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        forceClick(configurationMenu);
        WebElement customersMenu = driver.findElement(By.id("menu:menu"));
        forceClick(customersMenu);
        WebElement scriptMenu = driver.findElement(By.id("menu:customerBrands"));
        forceClick(scriptMenu);
    }
    
    /**
     * go to new page.
     * 
     * @param driver WebDriver
     */
    
    public void goNewPage(WebDriver driver) {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
        
        String test = "BR_" + System.currentTimeMillis();
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys(test);
        moveMouseAndClick(description);
        description.clear();
        description.sendKeys(test);
        WebElement btnSave = driver.findElement(By.id("formId:formButtonsCC:saveButton"));
        moveMouseAndClick(btnSave);
        moveMouseAndClick(codeToSearch);
        codeToSearch.clear();
        codeToSearch.sendKeys(test);
        moveMouseAndClick(searchBtn);
        moveMouseAndClick(elementToDelete);
        moveMouseAndClick(delete);
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
        
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
     * @return the searchBtn
     */
    public WebElement getSearchBtn() {
        return searchBtn;
    }
    
    /**
     * @param searchBtn the searchBtn to set
     */
    public void setSearchBtn(WebElement searchBtn) {
        this.searchBtn = searchBtn;
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
     * @return the elementToDelete
     */
    public WebElement getElementToDelete() {
        return elementToDelete;
    }
    
    /**
     * @param elementToDelete the elementToDelete to set
     */
    public void setElementToDelete(WebElement elementToDelete) {
        this.elementToDelete = elementToDelete;
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
