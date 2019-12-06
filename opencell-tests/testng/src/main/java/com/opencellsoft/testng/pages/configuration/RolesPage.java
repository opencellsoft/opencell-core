package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Roles page.
 * 
 * @author Fatine BELHADJ.
 *
 */
public class RolesPage extends BasePage {
    
    /**
     * button new.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement btnNew;
    /**
     * new user role.
     */
    @FindBy(id = "roleFormId:tabView:name_txt")
    private WebElement userRoleName;
    /**
     * new user role description.
     */
    @FindBy(id = "roleFormId:tabView:description_txt")
    private WebElement userRoleDescription;
    /**
     * button save.
     */
    @FindBy(id = "roleFormId:formButtonsCC:saveButtonAjax")
    private WebElement btnSave;
    /**
     * user role name.
     */
    @FindBy(id = "searchForm:name_txt")
    private WebElement searchFormCode;
    /**
     * user role description.
     */
    @FindBy(id = "searchForm:description_txt")
    private WebElement userRoleDescriptionSearch;
    /**
     * button search.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement buttonSearch;
    
   
    
    /**
     * button delete.
     */
    @FindBy(id = "formId:formButtonsCC:deletelink")
    private WebElement btnDelete;
    
    
   
    /**
     * Constructor.
     * 
     * @param driver webDriver
     */
    public RolesPage(final WebDriver driver) {
        super(driver);
    }
    
    /**
     * Opening roles page.
     * 
     * @param driver roles page.
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);
        
        WebElement rolesMenu = driver.findElement(By.id("menu:userRoles"));
        moveMouseAndClick(rolesMenu);
        
    }
    
    /**
     * Entering data.
     * 
     * @param driver roles page new.
     * @param data user data.
     * @throws InterruptedException
     */
    public void fillData(WebDriver driver, Map<String, String> data) throws InterruptedException {
        moveMouseAndClick(btnNew);
        moveMouseAndClick(userRoleName);
        userRoleName.clear();
        userRoleName.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(userRoleDescription);
        userRoleDescription.clear();
        userRoleDescription.sendKeys((String) data.get(Constants.DESCRIPTION));
        moveMouseAndClick(btnSave);
        moveMouseAndClick(searchFormCode);
        searchFormCode.clear();
        searchFormCode.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(buttonSearch);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement chartToDelete = driver
                    .findElement(By.id("datatable_results:0:resultsdeletelink"));
                moveMouseAndClick(chartToDelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
     
        
        WebElement confirmBtn = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmBtn);
        
    }
    
    public void setuserRoleName(WebElement userRoleName) {
        this.userRoleName = userRoleName;
    }
    
    public WebElement getCodeUser() {
        return this.userRoleName;
    }
    
    /**
     * new user role description setter.
     * 
     * @param userRoleDescription user description
     */
    public void setuserRoleDescription(WebElement userRoleDescription) {
        this.userRoleDescription = userRoleDescription;
    }
    
    /**
     * new user role description getter.
     * 
     * @return WebElement user description
     */
    public WebElement getUserRoleDescription() {
        return this.userRoleDescription;
    }
    
    /**
     * user role name search setter.
     * 
     * @param userRoleNameSearch setter
     */
    
    
    /**
     * user role description setter.
     * 
     * @param userRoleDescriptionSearch user description
     */
    public void setUserRoleDescriptionSearch(WebElement userRoleDescriptionSearch) {
        this.userRoleDescriptionSearch = userRoleDescriptionSearch;
    }
    
    /**
     * user role description getter.
     * 
     * @return user role description
     */
    public WebElement getuserRoleDescriptionSearch() {
        return this.userRoleDescriptionSearch;
    }
    
}
