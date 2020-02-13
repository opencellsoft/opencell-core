package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * 
 * @author Maria AIT BRAHIM
 *
 */
public class CustomersTitlesCivilitiesPage extends BasePage {

    /**
     * code.
     */
    @FindBy(id = "formId:code_txt")
    private WebElement codeCtp;

    /**
     * is company.
     */
    @FindBy(id = "formId:isCompany_bool")
    private WebElement isCompanyCtp;

    /**
     * Description.
     */
    @FindBy(id = "formId:description")
    private WebElement descriptionCtp;

    /**
     * english description.
     */
    private WebElement englishCtp;

    /**
     * frensh description.
     */
    private WebElement frenchshCtp;
    /**
     * code search.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeSearch;

    /**
     * button search.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBttn;

    /**
     * button delete.
     */
    @FindBy(id = "formId:formButtonsCC:deletelink")
    private WebElement deleteBttn;

    /**
     * @param driver WebDriver
     */

    public CustomersTitlesCivilitiesPage(WebDriver driver) {
        super(driver);
    }

    /**
     * click on configuration -> customers -> Titles and civilities.
     * 
     * @param driver WebDriver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);

        WebElement customersMenu = driver.findElement(By.id("menu:menu"));
        moveMouse(customersMenu);

        WebElement titlesSubMenu = driver.findElement(By.id("menu:titles"));
        moveMouseAndClick(titlesSubMenu);
    }

    /**
     * Go to New Titles and civilities.
     * 
     * @param driver WebDriver
     */
    public void gotoNewPage(final WebDriver driver) {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
    }

    /**
     * the button save.
     * 
     * @param driver WebDriver
     */

    public void saveTitles(WebDriver driver) {
        WebElement btnSave = driver.findElement(By.id("formId:formButtonsCC:saveButton"));
        moveMouseAndClick(btnSave);
    }

    /**
     * create new Titles and civilities.
     * 
     * @param driver WebDriver
     * @param data Map
     */
    public void fillFormCreate(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(getCodeCtp());
        getCodeCtp().clear();
        getCodeCtp().sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(getDescriptionCtp());
        getDescriptionCtp().clear();
        getDescriptionCtp().sendKeys((String) data.get(Constants.DESCRIPTION));
    }

    /**
     * Fill form and search.
     * 
     * @param driver WebDriver
     * @param data code
     */

    public void fillFormAndSearch(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(codeSearch);
        codeSearch.clear();
        codeSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(searchBttn);

    }

    /**
     * delete customer titles and civilities.
     * 
     * @param driver WebDriver
     * @throws InterruptedException 
     */
    public void deleteCalendar(WebDriver driver) throws InterruptedException {
        WebElement deleteRow = driver
            .findElement(By.id("datatable_results:0:code_id_message_link"));
        moveMouseAndClick(deleteRow);
        moveMouseAndClick(deleteBttn);
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);

    }

    /**
     * @return the codeCtp
     */
    public final WebElement getCodeCtp() {
        return codeCtp;
    }

    /**
     * @param codeCtp the codeCtp to set
     */
    public final void setCodeCtp(WebElement codeCtp) {
        this.codeCtp = codeCtp;
    }

    /**
     * @return the descriptionCtp
     */
    public final WebElement getDescriptionCtp() {
        return descriptionCtp;
    }

    /**
     * @param descriptionCtp the descriptionCtp to set
     */
    public final void setDescriptionCtp(WebElement descriptionCtp) {
        this.descriptionCtp = descriptionCtp;
    }

    /**
     * @return the englishCtp
     */
    public final WebElement getEnglishCtp() {
        return englishCtp;
    }

    /**
     * @param englishCtp the englishCtp to set
     */
    public final void setEnglishCtp(WebElement englishCtp) {
        this.englishCtp = englishCtp;
    }

    /**
     * @return the frenchshCtp
     */
    public final WebElement getFrenchshCtp() {
        return frenchshCtp;
    }

    /**
     * @param frenchshCtp the frenchshCtp to set
     */
    public final void setFrenchshCtp(WebElement frenchshCtp) {
        this.frenchshCtp = frenchshCtp;
    }

    /**
     * @return the codeSearch
     */
    public final WebElement getCodeSearch() {
        return codeSearch;
    }

    /**
     * @param codeSearch the codeSearch to set
     */
    public final void setCodeSearch(WebElement codeSearch) {
        this.codeSearch = codeSearch;
    }

}
