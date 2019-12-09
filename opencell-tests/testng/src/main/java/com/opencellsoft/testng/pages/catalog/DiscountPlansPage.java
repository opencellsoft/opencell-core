package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * @author Miftah
 *
 */
public class DiscountPlansPage extends BasePage {
    
    public DiscountPlansPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * click on catalog -> discount plans .
     * 
     * @param driver WebDriver
     * 
     */
    public void gotoListPage(final WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:catalog"));
        moveMouse(configurationMenu);
        
        WebElement pricePlanMenu = driver.findElement(By.id("menu:pricePlanMatrixes_menu"));
        moveMouse(pricePlanMenu);
        
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement discountPlansMenu = driver
                    .findElement(By.id("menu:discountPlans"));
                moveMouseAndClick(discountPlansMenu);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
       // WebElement discountPlansMenu = driver.findElement(By.id("menu:discountPlans"));
        
       // moveMouseAndClick(discountPlansMenu);
        
    }
    
    /**
     * fill the new Discount plan.
     * 
     * @param driver instance of WebDriver
     * @param data Map
     * @throws InterruptedException
     */
    
    public void fillFormCreate(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        WebElement bttnNew = driver.findElement(By.id("searchForm:buttonNew"));
        bttnNew.click();
        WebElement code = driver
            .findElement(By.id("tabView:formDiscountPlan:tabViewDiscountPlan:chargeSelectId"));
        // Code
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        WebElement description = driver
            .findElement(By.id("tabView:formDiscountPlan:tabViewDiscountPlan:description"));
        // Description
        moveMouseAndClick(description);
        description.clear();
        description.sendKeys((String) data.get(Constants.DESCRIPTION));
        WebElement bttnSave = driver
            .findElement(By.id("tabView:formDiscountPlan:formButtonsCC:saveButtonAjax"));
        moveMouseAndClick(bttnSave);
        WebElement discountPlanItems = driver
            .findElement(By.xpath("/html/body/div[2]/div/div[2]/div/ul/li[2]/a"));
        // click on discount plan tab
        moveMouseAndClick(discountPlanItems);
        
        WebElement discountPlanItemCode = driver
            .findElement(By.id("tabView:formDiscountPlanItem:tabViewDPI:code_txt"));
        moveMouseAndClick(discountPlanItemCode);
        discountPlanItemCode.sendKeys((String) data.get(Constants.CODE));
        
        // Percent
        WebElement percent = driver.findElement(
            By.id("tabView:formDiscountPlanItem:tabViewDPI:discountValuePercentage_input"));
        moveMouseAndClick(percent);
        percent.sendKeys("30");
        WebElement bttnAddThisDiscount = driver
            .findElement(By.id("tabView:formDiscountPlanItem:saveButton"));
        // click on button Add This Discount plan Item
        moveMouseAndClick(bttnAddThisDiscount);
        WebElement backButton = driver
            .findElement(By.id("tabView:formDiscountPlanItem:backButton"));
        // click on button back
        moveMouseAndClick(backButton);
        // Search and delete
        WebElement codeToSearch = driver.findElement(By.id("searchForm:code_txt"));
        codeToSearch.click();
        codeToSearch.sendKeys((String) data.get(Constants.CODE));
        
        WebElement BtnSearch = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(BtnSearch);
        
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
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement deletebttn = driver
                    .findElement(By.id("tabView:formDiscountPlan:formButtonsCC:deletelink"));
                moveMouseAndClick(deletebttn);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        yes.click();
        
    }
    
}
