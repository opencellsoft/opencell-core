package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * Service model page.
 * 
 * @author HP Fatine BELHADJ
 *
 */
public class ServiceModel extends BasePage {
    /**
     * button new.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement btnNew;
    /**
     * new code label.
     */
    @FindBy(id = "moduleForm:code_txt")
    private WebElement codeServiceModels;
    /**
     * new description label.
     */
    @FindBy(id = "moduleForm:description")
    private WebElement descriptionServiceModel;
    /**
     * service template.
     */
    @FindBy(id = "moduleForm:serviceSelectId_selectLink")
    private WebElement serviceTemplate;
    /**
     * module installation and activation script.
     */
    @FindBy(id = "moduleForm:script_selectLink")
    private WebElement moduleIandA;
    
    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public ServiceModel(final WebDriver driver) {
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
        WebElement servicemodels = driver.findElement(By.id("menu:businessServiceModels"));
        moveMouseAndClick(servicemodels);
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
        codeServiceModels.click();
        codeServiceModels.clear();
        codeServiceModels.sendKeys((String) data.get(Constants.CODE));
        
        descriptionServiceModel.click();
        descriptionServiceModel.clear();
        descriptionServiceModel.sendKeys((String) data.get(Constants.DESCRIPTION));
        
        serviceTemplate.click();
        moveMouseAndClick(driver.findElement(By.xpath("/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")));
        WebElement moduleIandA = driver.findElement(By.id("moduleForm:script_selectLink"));
        moveMouseAndClick(moduleIandA);
        WebElement moduleIA = driver.findElement(By.xpath("/html/body/div[10]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]"));
        moveMouseAndClick(moduleIA);
        WebElement btnSave = driver.findElements(By.className("ui-button-text-icon-left")).get(0);
        moveMouseAndClick(btnSave);   
        moveMouseAndClick(driver.findElements(By.className("ui-button-text-icon-left")).get(1));
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        yes.click();
    }
    
    /**
     * code setter.
     * 
     * @param codeOfferModels setter
     */
    public void setcodeOfferModels(WebElement codeOfferModels) {
        this.codeServiceModels = codeOfferModels;
    }
    
    /**
     * code getter.
     * 
     * @return code
     */
    public WebElement getcodeOfferModels() {
        return this.codeServiceModels;
    }
    
    /**
     * description setter.
     * 
     * @param descriptionOfferModel setter
     */
    public void setdescriptionOfferModel(WebElement descriptionOfferModel) {
        this.descriptionServiceModel = descriptionOfferModel;
    }
    
    /**
     * description getter.
     * 
     * @return description
     */
    public WebElement getdescriptionOfferModel() {
        return this.descriptionServiceModel;
    }
    
}
