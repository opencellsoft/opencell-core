package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Credit categories page.
 * 
 * @author Fatine BELHADJ
 *
 */
public class CreditCategoriesPage extends BasePage {
    
    /**
     * code credit new.
     */
    @FindBy(id = "formId:code_txt")
    private WebElement codeCreditNew;
    
    /**
     * credit description new.
     */
    @FindBy(id = "formId:description")
    private WebElement descriptionCreditNew;
    
    /**
     * code credit search.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeCredit;
    
    /**
     * description credit search.
     */
    @FindBy(id = "searchForm:description")
    private WebElement descriptionCredit;
    /**
     * button new.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement btnNew;
    /**
     * button reset.
     */
    @FindBy(id = "formId:formButtonsCC:resetButtonCC:resetButton")
    private WebElement btnReset;
    /**
     * button save.
     */
    @FindBy(id = "formId:formButtonsCC:saveButton")
    private WebElement btnSave;
    /**
     * button delete.
     */
    @FindBy(id = "datatable_results:0:resultsdeletelink")
    private WebElement btnDelete;
    
    /**
     * Constructor.
     * 
     * @param driver constructor
     */
    public CreditCategoriesPage(final WebDriver driver) {
        super(driver);
        
    }
    
    /**
     * Opening billing cycle page.
     * 
     * @param driver billing cycle
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);
        
        WebElement customerMenu = driver.findElement(By.id("menu:menu"));
        moveMouse(customerMenu);
        
        WebElement creditMenu = driver.findElement(By.id("menu:creditCategories"));
        moveMouseAndClick(creditMenu);
        
    }
    
    /**
     * Entering data.
     * 
     * @param driver new credit categories
     * @param data new credit categories
     * @throws InterruptedException
     */
    public void fillCreditCategoriNew(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(btnNew);
        moveMouseAndClick(codeCreditNew);
        codeCreditNew.clear();
        codeCreditNew.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(descriptionCreditNew);
        descriptionCreditNew.clear();
        descriptionCreditNew.sendKeys((String) data.get(Constants.DESCRIPTION));
        moveMouseAndClick(btnSave);
        moveMouseAndClick(codeCredit);
        codeCredit.clear();
        codeCredit.sendKeys((String) data.get(Constants.CODE));
        WebElement btnSearch = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(btnSearch);
        WebElement rowToDelete = driver
            .findElement(By.id("datatable_results:0:code_id_message_link"));
        moveMouseAndClick(rowToDelete);
        WebElement delete = driver.findElement(By.id("formId:formButtonsCC:deletelink"));
        moveMouseAndClick(delete);
        WebElement confirm = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirm);
        
    }
    
}
