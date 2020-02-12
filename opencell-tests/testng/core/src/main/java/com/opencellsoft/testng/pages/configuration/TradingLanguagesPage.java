package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Trading languages page.
 * 
 * @author Miftah
 * 
 *
 */
public class TradingLanguagesPage extends BasePage {
    /**
     * button reset.
     */
    @FindBy(id = "languagesFormId:formButtonsCC:resetButtonCC:resetButton")
    private WebElement btnReset;
    
    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public TradingLanguagesPage(final WebDriver driver) {
        super(driver);
    }
    
    /**
     * Opening trading languages page.
     * 
     * @param driver trading language.
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);
        
       // WebElement internationalSettings = driver.findElement(By.id("menu:trading"));
      //  moveMouse(internationalSettings);
        
        WebElement tradingLanguages = driver.findElement(By.id("menu:languages"));
        moveMouseAndClick(tradingLanguages);
        
    }
    
    /**
     * Selecting a trading language.
     * 
     * @param driver trading language.
     * @throws InterruptedException
     */
    public void languageSelect(WebDriver driver) throws InterruptedException {
        WebElement btnNew = driver.findElement((By.id("searchForm:buttonNew")));
        moveMouseAndClick(btnNew);
        WebElement description =driver.findElement((By.id("formId:descriptionEn_txt")));
        moveMouseAndClick(description);
        description.sendKeys("description");
        WebElement languageselect = driver
            .findElement(By.id("formId:languageCode_txt"));
        moveMouseAndClick(languageselect);
        languageselect.sendKeys("LAN");
       // WebElement language = driver.findElement((By.xpath("/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[6]/td[1]")));
       // waitUntilElementDisplayed(language, driver);
       // moveMouseAndClick(language);
        WebElement btnSave = driver.findElement((By.id("formId:formButtonsCC:saveButton")));
        moveMouseAndClick(btnSave);
        WebElement languagesearch = driver
                .findElement(By.id("searchForm:languageCode_txt"));
        moveMouseAndClick(languagesearch);
        languagesearch.sendKeys("LAN");
        WebElement btnSearch = driver.findElement((By.id("searchForm:buttonSearch")));
        moveMouseAndClick(btnSearch);
       
          //  WebElement rowTODelete = driver.findElement(By.id("datatable_results:2:resultsdeletelink"));
        //waitUntilElementDisplayed(rowTODelete, driver);
       // forceClick(rowTODelete);
        
       // WebElement btnDelete = driver.findElement(By.id("languagesFormId:formButtonsCC:deletelink"));
       // waitUntilElementDisplayed(btnDelete, driver);
        //forceClick(btnDelete);
        
    }
            public void delete(WebDriver driver) throws InterruptedException {
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
               // WebElement btnDelete = driver.findElement(By.id("languagesFormId:formButtonsCC:deletelink"));
               //  waitUntilElementDisplayed(btnDelete, driver);
               //  forceClick(btnDelete);
               // waitUntilElementDisplayed(deleteBttn, driver);
               // forceClick(deleteBttn);
                WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
                moveMouseAndClick(confirmDelete);
            }
        
       
    
    
}
