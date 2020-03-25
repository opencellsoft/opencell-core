package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Currencies page.
 * 
 * @author Fatine BELHADJ
 * 
 *
 */
public class Currencies extends BasePage {
    
    /**
     * button new.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement btnNew;
    /**
     * button reset.
     */
    @FindBy(id = "currenciesFormId:formButtonsCC:resetButtonCC:resetButton")
    private WebElement btnReset;
    /**
     * button save.
     */
    @FindBy(id = "currenciesFormId:formButtonsCC:saveButton")
    private WebElement btnSave;
    
    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public Currencies(final WebDriver driver) {
        super(driver);
    }
    /**
     * Entering data.
     * 
     * @param driver web driver
     * @throws InterruptedException
     */
    public void fillCurrencies(WebDriver driver) throws InterruptedException {
        WebElement btnNew = driver.findElement((By.id("searchForm:buttonNew")));
        moveMouseAndClick(btnNew);
        WebElement description =driver.findElement((By.id("formId:descriptionEn_txt")));
        moveMouseAndClick(description);
        description.sendKeys("descriptionL");
        WebElement languageselect = driver
            .findElement(By.id("formId:currencyCode_txt"));
        moveMouseAndClick(languageselect);
        languageselect.sendKeys("LLN");
        WebElement btnSave = driver.findElement((By.id("formId:formButtonsCC:saveButton")));
        moveMouseAndClick(btnSave);
        WebElement languagesearch = driver
                .findElement(By.id("searchForm:currencyCode_txt"));
        moveMouseAndClick(languagesearch);
        languagesearch.sendKeys("LLN");
        WebElement btnSearch = driver.findElement((By.id("searchForm:buttonSearch")));
        moveMouseAndClick(btnSearch);
        for(int i = 0; i < 2; i++) 
        {
         try

        {
         WebElement btnDelete = driver.findElement(By.id("datatable_results:0:resultsdeletelink"));
         moveMouseAndClick(btnDelete);
         break;
         }

        catch(StaleElementReferenceException see)

        {
         }
         }

        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
    }
    
    /**
     * Opening driver.
     * 
     * @param driver web driverF
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);
        
        //WebElement internationalSettings = driver.findElement(By.id("menu:trading"));
       // moveMouse(internationalSettings);
        
        WebElement tradingCurrencies = driver.findElement(By.id("menu:currencies"));
        moveMouseAndClick(tradingCurrencies);
        
    }
    
}
