package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * 
 * @author Maria AIT BRAHIM
 */
public class TaxesPage extends BasePage {

    /**
     * code.
     */
    @FindBy(id = "taxFormId:tabView:code_txt")
    private WebElement code;

    /**
     * Description.
     */
    @FindBy(id = "taxFormId:tabView:description")
    private WebElement description;

    /**
     * English field.
     */
    @FindBy(className = "ui-inputtext")
    private List<WebElement> englishField;

    /**
     * Frensh field.
     */
    @FindBy(className = "ui-inputtext")
    private List<WebElement> frenshField;

    /**
     * percent Number.
     */
    @FindBy(id = "taxFormId:tabView:percent_number")
    private WebElement percentNumber;

    /**
     * accounting Code.
     */
    @FindBy(id = "taxFormId:tabView:accountingCode_entity")
    private WebElement accountingCode;

    /**
     * selected accounting Code.
     */
    @FindBy(id = "taxFormId:tabView:accountingCode_entity_4")
    private WebElement selectedAccountingCode;

    /**
     * search button.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBttn;

    /**
     * code search.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeSearch;

    /**
     * button delete.
     */
    @FindBy(id = "taxFormId:formButtonsCC:deletelink")
    private WebElement deleteBttn;

    /**
     * button save.
     */
    @FindBy(id = "taxFormId:formButtonsCC:saveButton")
    private WebElement bttnSave;

    /**
     * button New.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement bttnNew;

    /**
     * 
     * @param driver instance of WebDriver
     */
    public TaxesPage(final WebDriver driver) {
        super(driver);
    }

    /**
     * click on configuration -> Billing -> Taxes.
     * 
     * @param driver WebDriver
     */

    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);

        WebElement internationalSettingMenu = driver.findElement(By.id("menu:invoicingconfig"));
        moveMouse(internationalSettingMenu);

        WebElement billingCyclesMenu = driver.findElement(By.id("menu:billingCycles"));
        moveMouse(billingCyclesMenu);

        WebElement taxesMenu = driver.findElement(By.id("menu:taxes"));
        moveMouseAndClick(taxesMenu);
    }

    /**
     * click on new Taxes.
     * 
     * @param driver WebDriver
     */
    public void gotoNewPage(WebDriver driver) {
        moveMouseAndClick(bttnNew);
    }

    /**
     * the button save.
     * 
     * @param driver WebDriver
     */
    public void save(WebDriver driver) {
        moveMouseAndClick(bttnSave);
    }

    /**
     * create new Taxe.
     * 
     * @param driver instance of WebDriver
     * @param data of mandatory fields
     */
    public void fillFormCreate(WebDriver driver, Map<String, String> data) {
        // Code
        moveMouseAndClick(getCode());
        getCode().clear();
        getCode().sendKeys((String) data.get(Constants.CODE));
        // Percent number
        moveMouseAndClick(percentNumber);
        percentNumber.clear();
        percentNumber.sendKeys("50");


    }

    /**
     * search for the new created Taxe.
     * 
     * @param driver instance of WebDriver
     * @param data of Map
     */

    public void fillFormAndSearch(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(codeSearch);
        codeSearch.clear();
        codeSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(searchBttn);
    }

    /**
     * delete Taxe.
     * 
     * @param driver WebDriver
     * @throws InterruptedException 
     */

    public void delete(WebDriver driver) throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement chartToDelete = driver
                    .findElement(By.id("datatable_results:0:code_id_message_link"));
                moveMouseAndClick(chartToDelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        moveMouseAndClick(deleteBttn);
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
    }

    /**
     * @return the code
     */
    public final WebElement getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public final void setCode(WebElement code) {
        this.code = code;
    }

    /**
     * @return the description
     */
    public final WebElement getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public final void setDescription(WebElement description) {
        this.description = description;
    }

}
