package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Country page.
 * 
 * @author Fatine BELHADJ.
 * 
 *
 */
public class Country extends BasePage {
    /**
     * button reset.
     */
    @FindBy(id = "formCountries:formButtonsCC:resetButtonCC:resetButton")
    private WebElement btnReset;
    /**
     * button new.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement btnNew;
    /**
     * button save.
     */
    @FindBy(id = "formCountries:formButtonsCC:saveButton")
    private WebElement btnSave;
    /**
     * button code.
     */
    @FindBy(id = "formCountries:countrySelectedId_selectLink")
    private WebElement btnCode;
    /**
     * button delete.
     */
    @FindBy(id = "datatable_results:0:resultsdeletelink")
    private WebElement btnDelete;

    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public Country(final WebDriver driver) {
        super(driver);
    }

    /**
     * clicking on button reset.
     * 
     * @param driver WebDriver
     */
    public void goTobtnReset(WebDriver driver) {
        waitUntilElementDisplayed(btnReset,driver);
        btnReset.click();
    }

    /**
     * clicking on button new.
     * 
     * @param driver WebDriver
     */
    public void goTobtnNew(WebDriver driver) {
        waitUntilElementDisplayed(btnNew,driver);
        btnNew.click();
    }

    /**
     * clicking on button save.
     * 
     * @param driver WebDriver
     */
    public void goTobtnSave(WebDriver driver) {
        waitUntilElementDisplayed(btnSave,driver);
        btnSave.click();
    }

    /**
     * entering data.
     * 
     * @param driver WebDriver
     */
    public void fillCountry(WebDriver driver) {
        waitUntilElementDisplayed(btnCode,driver);
        btnCode.click();
        waitUntilElementDisplayed(driver.findElement(By.cssSelector("tr.ui-datatable-even:nth-child(1)")),driver);
        driver.findElement(By.cssSelector("tr.ui-datatable-even:nth-child(1)")).click();
    }

    /**
     * Deleting data.
     * 
     * @param driver delete
     */
    public void delete(WebDriver driver) {
        waitUntilElementDisplayed(btnDelete,driver);
        btnDelete.click();
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        waitUntilElementDisplayed(yes,driver);
        yes.click();
    }

    /**
     * Opening country menu in configiration.
     * 
     * @param driver country
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);

        WebElement internationalSettings = driver.findElement(By.id("menu:trading"));
        moveMouse(internationalSettings);

        WebElement tradingCountries = driver.findElement(By.id("menu:tradingCountries"));
        moveMouseAndClick(tradingCountries);
    }

}
