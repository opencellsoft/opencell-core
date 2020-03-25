package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Maria AIT BRAHIM
 *
 */
public class EmailTemplatePage extends BasePage {

    /**
     * code.
     */
    @FindBy(id = "formId:code_txt")
    private WebElement codeEt;

    /**
     * Subject.
     */
    @FindBy(id = "formId:subject_txt")
    private WebElement subjectEt;

    /**
     * HTML content.
     */
    @FindBy(xpath = "/html/body")
    private WebElement contenthtml1;

    /**
     * text content.
     */
    @FindBy(id = "formId:textContent_txt")
    public WebElement textContent;

    /**
     * button search.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBttnCtp;

    /**
     * code search.
     */
    @FindBy(id = "searchForm:subject_txt")
    private WebElement codeSearch;

    /**
     * selected row for delete.
     */
    @FindBy(id = "datatable_results:0:subject_id_message_link")
    private WebElement deleteRow;

    /**
     * button delete.
     */
    @FindBy(id = "formId:formButtonsCC:deletelink")
    private WebElement deleteBttn;

    /**
     * Bold Button.
     */
    @FindBy(css = "div.ui-editor-group:nth-child(10) > div:nth-child(2)")
    private WebElement btnShowSource;

    /**
     * button save.
     */
    @FindBy(id = "formId:formButtonsCC:saveButton")
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
    public EmailTemplatePage(WebDriver driver) {
        super(driver);

    }

    /**
     * click on configuration -> international settings -> Email Templates.
     * 
     * @param driver WebDriver
     * @throws InterruptedException 
     * 
     */
    public void gotoListPage(final WebDriver driver) throws InterruptedException {
        
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);

        WebElement internationalSettingMenu = driver.findElement(By.id("menu:trading"));
        moveMouse(internationalSettingMenu);

        WebElement tradingLanguagesMenu = driver.findElement(By.id("menu:tradingLanguages"));
        moveMouse(tradingLanguagesMenu);

        WebElement emailMenu = driver.findElement(By.id("menu:emailTemplates"));
        moveMouseAndClick(emailMenu);

    }

    /**
     * click on newEmail Templates.
     * 
     * @param driver WebDriver
     */

    public void gotoNewPage(WebDriver driver) {
        waitUntilElementDisplayed(bttnNew,driver);
        bttnNew.click();
    }

    /**
     * the button save.
     * 
     * @param driver WebDriver
     */
    public void saveEmail(WebDriver driver) {
        waitUntilElementDisplayed(bttnSave,driver);
        forceClick(bttnSave);
    }

    /**
     * fill the new Email Template.
     * 
     * @param driver instance of WebDriver
     * @param data Map
     */

    public void fillFormCreate(WebDriver driver, Map<String, String> data) {
        // Code
        waitUntilElementDisplayed(getCodeEt(),driver);
        moveMouseAndClick(getCodeEt());
        getCodeEt().clear();
        getCodeEt().sendKeys((String) data.get(Constants.CODE));
        // Subject
        waitUntilElementDisplayed(getSubjectEt(),driver);
        moveMouseAndClick(getSubjectEt());
        getSubjectEt().clear();
        getSubjectEt().sendKeys((String) data.get(Constants.SUBJECT));
        waitUntilElementDisplayed(btnShowSource,driver);
        // click on show source button
        btnShowSource.click();
        waitUntilElementDisplayed(getContenthtml1(),driver);
        // fill the HTML Content
        moveMouseAndClick(getContenthtml1());
        getContenthtml1().sendKeys("Template example");
        waitUntilElementDisplayed(getTextContent(),driver);
        // fill the Text Content
        moveMouseAndClick(getTextContent());
        getTextContent().sendKeys("Cordialement");

    }

    /**
     * search for the new created Email Template.
     * 
     * @param driver instance of WebDriver
     * @param data Map
     */
    public void fillFormAndSearch(WebDriver driver, Map<String, String> data) {
        waitUntilElementDisplayed(codeSearch,driver);
        codeSearch.click();
        codeSearch.clear();
        codeSearch.sendKeys((String) data.get(Constants.CODE));
        waitUntilElementDisplayed(searchBttnCtp,driver);
        moveMouseAndClick(searchBttnCtp);
    }

    /**
     * delete Email Template.
     * 
     * @param driver WebDriver
     * @throws InterruptedException 
     */
    public void delete(WebDriver driver) throws InterruptedException {
        WebElement deleteRow = driver
            .findElement(By.id("datatable_results:0:resultsdeletelink"));
        waitUntilElementDisplayed(deleteRow,driver);
        forceClick(deleteRow);
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        waitUntilElementDisplayed(confirmDelete,driver);
        confirmDelete.click();
    }

    /**
     * @return the codeEt
     */
    public final WebElement getCodeEt() {
        return codeEt;
    }

    /**
     * @param codeEt the codeEt to set
     */
    public final void setCodeEt(WebElement codeEt) {
        this.codeEt = codeEt;
    }

    /**
     * @return the subjectEt
     */
    public final WebElement getSubjectEt() {
        return subjectEt;
    }

    /**
     * @param subjectEt the subjectEt to set
     */
    public final void setSubjectEt(WebElement subjectEt) {
        this.subjectEt = subjectEt;
    }

    /**
     * @return the contenthtml1
     */
    public final WebElement getContenthtml1() {
        return contenthtml1;
    }

    /**
     * @param contenthtml1 the contenthtml1 to set
     */
    public final void setContenthtml1(WebElement contenthtml1) {
        this.contenthtml1 = contenthtml1;
    }

    /**
     * @return the textContent
     */
    public final WebElement getTextContent() {
        return textContent;
    }

    /**
     * @param textContent the textContent to set
     */
    public final void setTextContent(WebElement textContent) {
        this.textContent = textContent;
    }

}
