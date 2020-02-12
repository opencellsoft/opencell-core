package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * @author Edward P. Legaspi
 * 
 */
public class ServiceTemplateDetailPage extends BasePage {

    @FindBy(id = "tabView:formId:code_txt")
    public WebElement codeIpt;

    @FindBy(id = "tabView:formId:description_txt")
    public WebElement descriptionIpt;

    @FindBy(id = "tabView:formId:longDescription")
    public WebElement longDescriptionIpt;

    @FindBy(id = "tabView:formId:formButtonsCC:saveButtonAjax")
    public WebElement saveBtn;

    /**
     * @param driver web driver.
     */
    public ServiceTemplateDetailPage(WebDriver driver) {
        super(driver);
    }

    /**
     * @param driver web driver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement catalogMenu = driver.findElement(By.id("menu:catalog"));
        Actions action = new Actions(driver);
        action.moveToElement(catalogMenu).build().perform();

        WebElement serviceManagementMenu = driver.findElement(By.id("menu:serviceManagement"));
        action = new Actions(driver);
        action.moveToElement(serviceManagementMenu).build().perform();

        WebElement chargesMenu = driver.findElement(By.id("menu:serviceTemplates"));
        chargesMenu.click();
    }

    /**
     * @param driver web driver
     * @param data mapping data.
     * @throws InterruptedException 
     */
    public void fillFormAndSave(WebDriver driver, Map<String, String> data) throws InterruptedException {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        btnNew.click();
        codeIpt.click();
        codeIpt.clear();
        codeIpt.sendKeys((String) data.get(Constants.CODE));

        descriptionIpt.click();
        descriptionIpt.clear();
        descriptionIpt.sendKeys((String) data.get(Constants.DESCRIPTION));

        longDescriptionIpt.click();
        longDescriptionIpt.clear();
        longDescriptionIpt.sendKeys((String) data.get(Constants.LONG_DESCRIPTION));
        
        forceClick(saveBtn);
    }

}
