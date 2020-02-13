package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Hassnaa MIFTAH
 */

public class CustomerCategoriesPage extends BasePage {

    /**
     * code.
     */
    @FindBy(id = "formId:tabView:code_txt")
    private WebElement codecustcat;

    /**
     * description
     */
    @FindBy(id = "formId:tabView:description")
    private WebElement descriptioncustcat;

    /**
     * no tax applied.
     */
    @FindBy(id = "formId:exoneratedFromTaxes_bool")
    private WebElement nta;

    /**
     * no tax applied el.
     */
    @FindBy(id = "formId:exonerationTaxEl_txt")
    private WebElement ntael;

    /**
     * no tax reason.
     */
    @FindBy(id = "formId:exonerationReason_txt")
    private WebElement ntr;

    /**
     * search button.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchCp;

    /**
     * code to search.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement searchcodeCp;

    /**
     * delete button.
     */
    @FindBy(id = "formId:formButtonsCC:deletelink")
    private WebElement delete;

    /**
     * select an element from the datatable.
     */
    @FindBy(id = "datatable_results:0:code_id_message_link")
    private WebElement tableElement;

    /**
     * constructor.
     * 
     * @param driver
     */
    public CustomerCategoriesPage(WebDriver driver) {
        super(driver);
    }

    /**
     * go to configuration -> customer categories.
     * 
     * @param driver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);

        WebElement customerMenu = driver.findElement(By.id("menu:menu"));
        moveMouse(customerMenu);

        WebElement titlesSubMenu = driver.findElement(By.id("menu:titles"));
        moveMouse(titlesSubMenu);

        WebElement customerCategoriesMenu = driver.findElement(By.id("menu:customerCategories"));
        moveMouseAndClick(customerCategoriesMenu);
    }

    /**
     * gotoNewPage.
     * 
     * @param driver
     */
    public void gotoNewPage(WebDriver driver) {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
    }

    /**
     * save method.
     * 
     * @param driver webdriver
     */
    public void gotoSave(WebDriver driver) {
        WebElement btnSave = driver.findElement(By.id("formId:formButtonsCC:saveButton"));
        moveMouseAndClick(btnSave);

    }

    /**
     * method fillForm.
     * 
     * @param driver webdriver
     * @param data map
     */
    public void fillForm(WebDriver driver, Map<String, String> data) {
        /**
         * fill the code of a customer category.
         */
        moveMouseAndClick(codecustcat);
        codecustcat.clear();
        codecustcat.sendKeys((String) data.get(Constants.CODE));
        /**
         * fill the description of a customer category.
         */
        moveMouseAndClick(descriptioncustcat);
        descriptioncustcat.clear();
        descriptioncustcat.sendKeys((String) data.get(Constants.DESCRIPTION));
    }

    /**
     * search and delete
     * 
     * @param driver webdriver
     * @param CD map
     * @throws InterruptedException 
     */
    public void searchandDelete(WebDriver driver, Map<String, String> CD) throws InterruptedException {

        /**
         * enter a code of customer category to find.
         */
        moveMouseAndClick(searchcodeCp);
        searchcodeCp.clear();
        searchcodeCp.sendKeys((String) CD.get(Constants.CODE));
        /**
         * click on search button.
         */
        WebElement searchCustCat = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(searchCustCat);
        /**
         * select the element from the data table.
         */
        moveMouseAndClick(tableElement);
        /**
         * click on delete.
         */
        moveMouseAndClick(delete);
        
        /**
         * click on confirm.
         */
        WebElement confirm = driver.findElements(By.className("ui-button")).get(0);
        moveMouseAndClick(confirm);

    }

    /**
     * @return the codecustcat
     */
    public WebElement getCodecustcat() {
        return codecustcat;
    }

    /**
     * @param codecustcat the codecustcat to set
     */
    public void setCodecustcat(WebElement codecustcat) {
        this.codecustcat = codecustcat;
    }

    /**
     * @return the descriptioncustcat
     */
    public WebElement getDescriptioncustcat() {
        return descriptioncustcat;
    }

    /**
     * @param descriptioncustcat the descriptioncustcat to set
     */
    public void setDescriptioncustcat(WebElement descriptioncustcat) {
        this.descriptioncustcat = descriptioncustcat;
    }

    /**
     * @return the nta
     */
    public WebElement getNta() {
        return nta;
    }

    /**
     * @param nta the nta to set
     */
    public void setNta(WebElement nta) {
        this.nta = nta;
    }

    /**
     * @return the ntael
     */
    public WebElement getNtael() {
        return ntael;
    }

    /**
     * @param ntael the ntael to set
     */
    public void setNtael(WebElement ntael) {
        this.ntael = ntael;
    }

    /**
     * @return the ntr
     */
    public WebElement getNtr() {
        return ntr;
    }

    /**
     * @param ntr the ntr to set
     */
    public void setNtr(WebElement ntr) {
        this.ntr = ntr;
    }

    /**
     * @return the searchCp
     */
    public WebElement getSearchCp() {
        return searchCp;
    }

    /**
     * @param searchCp the searchCp to set
     */
    public void setSearchCp(WebElement searchCp) {
        this.searchCp = searchCp;
    }

    /**
     * @return the searchcodeCp
     */
    public WebElement getSearchcodeCp() {
        return searchcodeCp;
    }

    /**
     * @param searchcodeCp the searchcodeCp to set
     */
    public void setSearchcodeCp(WebElement searchcodeCp) {
        this.searchcodeCp = searchcodeCp;
    }

    /**
     * @return the delete
     */
    public WebElement getDelete() {
        return delete;
    }

    /**
     * @param delete the delete to set
     */
    public void setDelete(WebElement delete) {
        this.delete = delete;
    }

    /**
     * @return the tableElement
     */
    public WebElement getTableElement() {
        return tableElement;
    }

    /**
     * @param tableElement the tableElement to set
     */
    public void setTableElement(WebElement tableElement) {
        this.tableElement = tableElement;
    }

}