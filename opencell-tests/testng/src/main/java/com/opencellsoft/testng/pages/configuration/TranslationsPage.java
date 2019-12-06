package com.opencellsoft.testng.pages.configuration;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.opencellsoft.testng.pages.BasePage;
import org.openqa.selenium.support.FindBy;

/**
 * 
 * @author Hassnaa MIFTAH
 */
public class TranslationsPage extends BasePage {

    /**
     * locate the dropDown list.
     */

    private WebElement objectType;
    /**
     * element from the dropDown list.
     */
    private WebElement objectChoice;

    /**
     * description.
     */
    @FindBy(id = "entityForm:description")
    private WebElement description;

    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public TranslationsPage(final WebDriver driver) {
        super(driver);
    }

    /**
     * go to configuration -> Translations.
     * 
     * @param driver WebDriver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);

        WebElement interSettings = driver.findElement(By.id("menu:trading"));
        moveMouse(interSettings);

        WebElement translations = driver.findElement(By.id("menu:multiLanguageFields"));
        moveMouseAndClick(translations);

    }

    /**
     * method to save the information on the popUp.
     * 
     * @param driver WebDriver
     */
    public void save(WebDriver driver) {
        WebElement saveBtn = driver.findElements(By.className("ui-button-text-only")).get(2);
        waitUntilElementDisplayed(saveBtn, driver);
        saveBtn.click();
    }

    /**
     * method that choose an object from the dropDow list.
     * 
     * @param driver WebDriver
     */
    public void chooseObject(WebDriver driver) {

        WebElement objectType = driver.findElement(By.className("ui-selectonemenu-label"));
        waitUntilElementDisplayed(objectType, driver);
        forceClick(objectType);

        WebElement objectChoice = driver.findElements(By.className("ui-selectonemenu-item")).get(5);
        waitUntilElementDisplayed(objectChoice, driver);
        forceClick(objectChoice);

    }

    /**
     * method that fill the popUp.
     * 
     * @param driver WebDriver
     * @param data Map
     */
    public void fillPopUp(WebDriver driver, Map<String, String> data) {

        WebElement clickOnElement = driver
            .findElement(By.cssSelector("tr.ui-widget-content:nth-child(1) > td:nth-child(1)"));
        waitUntilElementDisplayed(clickOnElement, driver);
        clickOnElement.click();
        waitUntilElementDisplayed(description, driver);
        moveMouseAndClick( description);
        description.clear();
        description.sendKeys("this is another description");

    }

    /**
     * @return the objectType
     */
    public WebElement getObjectType() {
        return objectType;
    }

    /**
     * @param objectType the objectType to set
     */
    public void setObjectType(WebElement objectType) {
        this.objectType = objectType;
    }

    /**
     * @return the objectChoice
     */
    public WebElement getObjectChoice() {
        return objectChoice;
    }

    /**
     * @param objectChoice the objectChoice to set
     */
    public void setObjectChoice(WebElement objectChoice) {
        this.objectChoice = objectChoice;
    }

    /**
     * @return the description
     */
    public WebElement getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(WebElement description) {
        this.description = description;
    }
}
