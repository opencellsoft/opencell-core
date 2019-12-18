package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Termination reasons.
 * 
 * @author Fatine BELHADJ.
 *
 *
 */
public class TerminationReasons extends BasePage {
    /**
     * button new.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement btnNew;
    /**
     * new code label.
     */
    @FindBy(id = "formId:code_txt")
    private WebElement codeTerminationReasons;
    /**
     * new description.
     */
    @FindBy(id = "formId:description_txt")
    private WebElement descriptionTerminationReasons;
    /**
     * button save.
     */
    @FindBy(id = "formId:formButtonsCC:saveButton")
    private WebElement btnSave;
    /**
     * code label.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeSearch;
    /**
     * description label.
     */
    @FindBy(id = "searchForm:description_txt")
    private WebElement descriptionSearch;
    /**
     * search button.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement search;
    /**
     * delete button.
     */
    @FindBy(id = "datatable_results:0:resultsdeletelink")
    private WebElement btnDelete;

    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public TerminationReasons(final WebDriver driver) {
        super(driver);
    }

    /**
     * Opening termination reasons.
     * 
     * @param driver termination reasons.
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);
        
        WebElement terminationReasons = driver.findElement(By.id("menu:terminationReasons"));
        moveMouseAndClick(terminationReasons);

    }

    /**
     * clicking on button new.
     * 
     * @param driver WebDriver
     */
    public void goTobtnNew(WebDriver driver) {
        moveMouseAndClick(btnNew);
    }

    /**
     * Entering data.
     * 
     * @param driver termination reasons.
     * @param data termination reasons data.
     */
    public void fillData(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(codeTerminationReasons);
        codeTerminationReasons.clear();
        codeTerminationReasons.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(descriptionTerminationReasons);
        descriptionTerminationReasons.clear();
        descriptionTerminationReasons.sendKeys((String) data.get(Constants.DESCRIPTION));
    }

    /**
     * clicking on save button.
     * 
     * @param driver WebDriver
     */
    public void goToSave(WebDriver driver) {
        moveMouseAndClick(btnSave);
    }

    /**
     * Entering data search.
     * 
     * @param driver termination reasons.
     * @param data data to search.
     */
    public void fillAndSearche(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(codeSearch);
        codeSearch.clear();
        codeSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(descriptionSearch);
        descriptionSearch.clear();
        descriptionSearch.sendKeys((String) data.get(Constants.DESCRIPTION));
        moveMouseAndClick(search);

    }
    /**
     * deleting data.
     * 
     * @param driver driver.
     */
    public void delete(WebDriver driver) {
        moveMouseAndClick(btnDelete);
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
    }

    /**
     * code termination setter.
     * 
     * @param codeTerminationReasons setter
     */
    public void setcodeTerminationReasonsr(WebElement codeTerminationReasons) {
        this.codeTerminationReasons = codeTerminationReasons;
    }

    /**
     * code termination getter.
     * 
     * @return code termination
     */
    public WebElement getcodeTerminationReasons() {
        return this.codeTerminationReasons;
    }

    /**
     * description setter.
     * 
     * @param descriptionTerminationReasons setter
     */
    public void setdescriptionTerminationReasons(WebElement descriptionTerminationReasons) {
        this.descriptionTerminationReasons = descriptionTerminationReasons;
    }

    /**
     * description getter.
     * 
     * @return description
     */
    public WebElement getdescriptionTerminationReasons() {
        return this.descriptionTerminationReasons;
    }

}
