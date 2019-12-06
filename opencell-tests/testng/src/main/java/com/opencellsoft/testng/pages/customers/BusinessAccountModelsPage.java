package com.opencellsoft.testng.pages.customers;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class BusinessAccountModelsPage extends BasePage {
    @FindBy(id = "searchForm:buttonNew")
    private WebElement buttonNew;
    
    @FindBy(id = "moduleForm:code_txt")
    private WebElement codeModel;
    
    @FindBy(id = "moduleForm:hierarchyType_enum_label")
    private WebElement typeList;
    
    @FindBy(id = "moduleForm:hierarchyType_enum_1")
    private WebElement type;
    
    @FindBy(id = "moduleForm:description")
    private WebElement description;
    
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeToSearch;
    
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement buttonSearch;

    
    public BusinessAccountModelsPage(WebDriver driver) {
        super(driver);
    }
    
    public void gotoListPage(WebDriver driver) {
        WebElement customersMenu = driver.findElement(By.id("menu:crm"));
        moveMouse(customersMenu);
        
        WebElement subbscription = driver.findElement(By.id("menu:businessAccountModels"));
        moveMouseAndClick(subbscription);
    }
    
    public void fillFormbusinessAccountModels(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(buttonNew);
        moveMouseAndClick(codeModel);
        codeModel.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(description);
        description.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(typeList);
        moveMouseAndClick(type);
        WebElement buttonSave = driver.findElements(By.className("ui-button-text-icon-left"))
            .get(0);
        moveMouseAndClick(buttonSave);
        WebElement customersMenu = driver.findElement(By.id("menu:crm"));
        moveMouse(customersMenu);
        WebElement subbscription = driver.findElement(By.id("menu:businessAccountModels"));
        moveMouseAndClick(subbscription);
        moveMouseAndClick(codeToSearch);
        codeToSearch.clear();
        codeToSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(buttonSearch);        
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement deleteButton = driver
                    .findElement(By.id("datatable_results:0:resultsdeletelink"));
                moveMouseAndClick(deleteButton);
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
     * @return the codeModel
     */
    public WebElement getCodeModel() {
        return codeModel;
    }

    /**
     * @param codeModel the codeModel to set
     */
    public void setCodeModel(WebElement codeModel) {
        this.codeModel = codeModel;
    }

    /**
     * @return the typeList
     */
    public WebElement getTypeList() {
        return typeList;
    }

    /**
     * @param typeList the typeList to set
     */
    public void setTypeList(WebElement typeList) {
        this.typeList = typeList;
    }

    /**
     * @return the type
     */
    public WebElement getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(WebElement type) {
        this.type = type;
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

}
