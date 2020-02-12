package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * @author Hassnaa Miftah
 *
 */
public class OffersPage extends BasePage {
    
    /**
     * offer's name .
     */
    @FindBy(id = "offerForm:tabView:offerInfo:name")
    private WebElement name;
    
    /**
     * constructor .
     * 
     * @param driver WebDriver
     */
    public OffersPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * go to Catalog > Offer Management > Offers
     * 
     * @param driver WebDriver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:catalog"));
        moveMouse(configurationMenu);
        
        WebElement offerManagementMenu = driver.findElement(By.id("menu:offerManagement"));
        moveMouseAndClick(offerManagementMenu);
        
        WebElement offersMenu = driver.findElement(By.id("menu:offerTemplates"));
        moveMouseAndClick(offersMenu);
        
    }
    /**
     * Fill the New Offer Template.
     * 
     * @param driver WebDriver
     * @throws InterruptedException exception
     */
    public void fillInformationsForm(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
        WebElement code = driver.findElement(By.id("offerForm:tabView:offerInfo:code_txt"));
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(name);
        name.clear();
        name.sendKeys((String) data.get(Constants.CODE));
        WebElement lifeCycleStatus = driver.findElement(By.xpath("/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/div/div[1]/span/table/tbody/tr/td[2]/div/div/table/tbody/tr[4]/td/div/div/div/div[3]/span"));
        moveMouseAndClick(lifeCycleStatus);
        WebElement activeStatus = driver.findElement(By.id("offerForm:tabView:offerInfo:lifeCycleStatus_1"));
        moveMouseAndClick(activeStatus);
        WebElement serviceTab = driver.findElement(By.xpath("/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/ul/li[2]/a"));
        moveMouseAndClick(serviceTab);
        
        WebElement addNewService = driver.findElements(By.className("ui-button-text-only")).get(2);
        moveMouseAndClick(addNewService);
        WebElement serviceTemplate =driver.findElement(By.id("offerForm:tabView:serviceTemplateCode_selectLink"));
        moveMouseAndClick(serviceTemplate);
        WebElement choosenService = driver.findElement(By.xpath("/html/body/div[10]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td"));
        moveMouseAndClick(choosenService);
        WebElement addSave = driver.findElements(By.className("ui-button-text-only")).get(3);
        moveMouseAndClick(addSave);
        WebElement saveBtn = driver.findElement(By.id("offerForm:formButtonsCC:saveButtonAjax"));
        moveMouseAndClick(saveBtn);
        WebElement backBtn = driver.findElement(By.id("offerForm:formButtonsCC:backButton"));
        moveMouseAndClick(backBtn);     
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement searchForm = driver
                    .findElement(By.id("searchForm:code_txt"));
                moveMouseAndClick(searchForm);
                searchForm.sendKeys((String) data.get(Constants.CODE));
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        WebElement btnSearch = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(btnSearch);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement rowTODelete = driver
                    .findElement(By.id("datatable_results:0:code_id_message_link"));
                moveMouseAndClick(rowTODelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        WebElement deletebttn = driver.findElement(By.id("offerForm:formButtonsCC:deletelink"));
        deletebttn.click();

        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
      }

    /**
     * @return the name
     */
    public WebElement getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(WebElement name) {
        this.name = name;
    }
}