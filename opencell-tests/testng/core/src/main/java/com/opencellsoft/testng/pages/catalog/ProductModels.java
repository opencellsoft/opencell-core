package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * Product model page.
 * 
 * @author Miftah
 *
 */
public class ProductModels extends BasePage {
    /**
     * button new.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement btnNew;
    /**
     * new code label.
     */
    @FindBy(id = "moduleForm:code_txt")
    private WebElement codeProductModels;
    /**
     * new description label.
     */
    @FindBy(id = "moduleForm:description")
    private WebElement descriptionProductModel;
    /**
     * product code.
     */
    @FindBy(id = "moduleForm:productSelectId_selectLink")
    private WebElement productCode;
    /**
     * module installation and activation script.
     */
    @FindBy(id = "moduleForm:script_selectLink")
    private WebElement moduleIandA;
    /**
     * add entity.
     */
    @FindBy(id = "moduleForm:moduleItems:j_idt296")
    private WebElement addEntity;
    /**
     * select an entity.
     */
    @FindBy(id = "addModuleItempopupForm:searchEntityClass_label")
    private WebElement selectEntity;
    /**
     * choosing an entity type.
     */
    @FindBy(id = "addModuleItempopupForm:searchEntityClass_5")
    private WebElement entityType;
    
    /**
     * button delete.
     */
    @FindBy(id = "moduleForm:j_idt308:deletelink")
    private WebElement btnDelete;
    
    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public ProductModels(final WebDriver driver) {
        super(driver);
    }
    
    /**
     * Opening offer model menu.
     * 
     * @param driver WebDriver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement catalogMenu = driver.findElement(By.id("menu:catalog"));
        moveMouseAndClick(catalogMenu);
        WebElement modelsMenu = driver.findElement(By.id("menu:models"));
        moveMouseAndClick(modelsMenu);
        WebElement productmodels = driver.findElement(By.id("menu:businessProductModels"));
        moveMouseAndClick(productmodels);
    }
    
    /**
     * entering data.
     * 
     * @param driver WebDriver
     * @param data code, description, code, entity
     * @throws InterruptedException
     */
    public void fillData(WebDriver driver, Map<String, String> data) throws InterruptedException {
        btnNew.click();
        codeProductModels.click();
        codeProductModels.clear();
        codeProductModels.sendKeys((String) data.get(Constants.CODE));
        
        descriptionProductModel.click();
        descriptionProductModel.clear();
        descriptionProductModel.sendKeys((String) data.get(Constants.DESCRIPTION));
        
        productCode.click();
        moveMouseAndClick(driver.findElement(
            By.xpath("/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")));
        
        moveMouseAndClick(driver.findElements(By.className("ui-button-text-icon-left")).get(0));
        
        moveMouseAndClick(driver.findElements(By.className("ui-button-text-icon-left")).get(1));
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
    }
    
    /**
     * code setter.
     * 
     * @param codeOfferModels setter
     */
    public void setcodeOfferModels(WebElement codeOfferModels) {
        this.codeProductModels = codeOfferModels;
    }
    
    /**
     * code getter.
     * 
     * @return code
     */
    public WebElement getcodeOfferModels() {
        return this.codeProductModels;
    }
    
    /**
     * description setter.
     * 
     * @param descriptionOfferModel setter
     */
    public void setdescriptionOfferModel(WebElement descriptionOfferModel) {
        this.descriptionProductModel = descriptionOfferModel;
    }
    
    /**
     * description getter.
     * 
     * @return description
     */
    public WebElement getdescriptionOfferModel() {
        return this.descriptionProductModel;
    }
    
}
