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
 * entity customization page.
 * 
 * @author MIFTAH
 *
 */
public class EntityCustomizationPage extends BasePage {
    /**
     * button new.
     */
    @FindBy(id = "searchPanel:buttonNew")
    private WebElement btnNew;
    /**
     * new code label.
     */
    @FindBy(id = "cetForm:code_txt")
    private WebElement codeEntity;
    /**
     * new description label.
     */
    @FindBy(id = "cetForm:description_txt")
    private WebElement descriptionEntity;
    /**
     * title name.
     */
    @FindBy(id = "cetForm:name_txt")
    private WebElement titleName;
    /**
     * button save.
     */
    @FindBy(id = "cetForm:formButtonsCC:saveButton")
    private WebElement btnSave;

    
    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public EntityCustomizationPage(final WebDriver driver) {
        super(driver);
    }
    
    /**
     * Opening offer model menu.
     * 
     * @param driver WebDriver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement administrationMenu = driver.findElement(By.id("menu:automation"));
        moveMouse(administrationMenu);
        WebElement entityCustomization = driver.findElement(By.id("menu:customizedEntities"));
        moveMouseAndClick(entityCustomization);
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
     */
    public void fillData(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(codeEntity);
        codeEntity.clear();
        codeEntity.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(descriptionEntity);
        descriptionEntity.clear();
        descriptionEntity.sendKeys((String) data.get(Constants.DESCRIPTION));
        moveMouseAndClick(titleName);
        titleName.clear();
        titleName.sendKeys((String) data.get(Constants.DESCRIPTION));
        
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
    public void delete(WebDriver driver) {
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement btnDelete = driver
                    .findElement(By.id("cetForm:formButtonsCC:deletelink"));
                moveMouseAndClick(btnDelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
    }
    
    /**
     * code setter.
     * 
     * @param codeEntity setter
     */
    public void setcodeEntity(WebElement codeEntity) {
        this.codeEntity = codeEntity;
    }
    
    /**
     * code getter.
     * 
     * @return code
     */
    public WebElement getcodeEntity() {
        return this.codeEntity;
    }
    
    /**
     * description setter.
     * 
     * @param descriptionEntity setterF
     */
    public void setdescriptionEntity(WebElement descriptionEntity) {
        this.descriptionEntity = descriptionEntity;
    }
    
    /**
     * description getter.
     * 
     * @return description
     */
    public WebElement gettitleName() {
        return this.titleName;
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
     * @return the codeEntity
     */
    public WebElement getCodeEntity() {
        return codeEntity;
    }
    
    /**
     * @param codeEntity the codeEntity to set
     */
    public void setCodeEntity(WebElement codeEntity) {
        this.codeEntity = codeEntity;
    }
    
    /**
     * @return the descriptionEntity
     */
    public WebElement getDescriptionEntity() {
        return descriptionEntity;
    }
    
    /**
     * @param descriptionEntity the descriptionEntity to set
     */
    public void setDescriptionEntity(WebElement descriptionEntity) {
        this.descriptionEntity = descriptionEntity;
    }
    
    /**
     * @return the titleName
     */
    public WebElement getTitleName() {
        return titleName;
    }
    
    /**
     * @param titleName the titleName to set
     */
    public void setTitleName(WebElement titleName) {
        this.titleName = titleName;
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
}
