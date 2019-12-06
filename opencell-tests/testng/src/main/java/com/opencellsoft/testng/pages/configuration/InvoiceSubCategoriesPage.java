package com.opencellsoft.testng.pages.configuration;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import org.openqa.selenium.support.FindBy;

/**
 * 
 * @author Hassnaa MIFTAH.
 */
public class InvoiceSubCategoriesPage extends BasePage {

    /**
     * element choosen to be deleted .
     */
    @FindBy(id = "searchForm:invoiceCategoryField_1")
    private WebElement invSubCatSearchChoice;

    /**
     * description.
     */
    @FindBy(id = "searchForm:description")
    private WebElement descriptionInvSubCat;

    /**
     * invoice category choosen.
     */
    @FindBy(id = "tabView:formId:categorySelectId_1")
    private WebElement invSubCatChoice;

    /**
     * invoice category dropdown list.
     */
    @FindBy(id = "tabView:formId:categorySelectId_label")
    private WebElement invoiceSubCat;

    /**
     * code
     */
    @FindBy(id = "tabView:formId:code_txt")
    private WebElement invoiceSubCatCode;

    /**
     * accounting code .
     */
    @FindBy(id = "tabView:formId:accountingCode_entity_label")
    private WebElement codeComptable;

    /**
     * accouting code choosen from teh dropdown list.
     */
    @FindBy(id = "tabView:formId:accountingCode_entity_1")
    private WebElement codeComptableChoice;

    /**
     * description.
     */
    @FindBy(id = "tabView:formId:description")
    private WebElement descriptionInvSub;

    /**
     * english field.
     */
    private WebElement englishInvSubCat;

    /**
     * frensh field.
     */
    private WebElement frenchshInvSubCat;

    /**
     * invoice sub category choosen after search .
     */
    @FindBy(id = "searchForm:invoiceCategoryField_0")
    private WebElement invoiceSubCatChoiceSearch;

    /**
     * search button.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBtn;

    /**
     * invoice category dropdown list.
     */
    @FindBy(id = "searchForm:invoiceCategoryField_label")
    private WebElement invoiceSubCatSearch;

    /**
     * invoice category to delete.
     */
    @FindBy(id = "datatable_results:0:code_id_message_link")
    private WebElement deleteRow;

    /**
     * delete button.
     */
    @FindBy(id = "tabView:formId:formButtonsCC:deletelink")
    private WebElement delete;

    /**
     * constructor.
     * 
     * @param driver
     */
    public InvoiceSubCategoriesPage(WebDriver driver) {
        super(driver);
    }

    /**
     * go to the configuration -> invoice sub categories.
     * 
     * @param driver web driver
     */

    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);

        WebElement invConfigMenu = driver.findElement(By.id("menu:invoicingconfig"));
        moveMouse(invConfigMenu);

        WebElement invSubCatMenu = driver.findElement(By.id("menu:invoiceSubCategories"));
        moveMouseAndClick(invSubCatMenu);

    }

    /**
     * method to click on new button.
     * 
     * @param driver web driver.
     */
    public void gotoNewPage(WebDriver driver) {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
    }

    public void saveInvCat(WebDriver driver) {
        WebElement saveBttnInvCat = driver
            .findElement(By.id("tabView:formId:formButtonsCC:saveButton"));
        moveMouseAndClick(saveBttnInvCat);
    }
    /**
     * fill Form method.
     * 
     * @param drive,data
     */

    public void fillFormCreate(WebDriver driver, Map<String, String> data) {
        /**
         * select a sub category from the drop down list.
         */
        moveMouseAndClick(invoiceSubCat);
        invSubCatChoice.click();

        /**
         * fill the code of the sub category.
         */
        moveMouseAndClick(invoiceSubCatCode);
        invoiceSubCatCode.clear();
        invoiceSubCatCode.sendKeys((String) data.get(Constants.CODE));

       

    }

    /**
     * go back on back Button.
     * 
     * @param driver
     */
    public void GoBack(WebDriver driver) {

        WebElement back = driver.findElement(By.id("tabView:formId:formButtonsCC:backButton"));
        moveMouseAndClick(back);
    }

    /**
     * Searching and deleting sub categories.
     * 
     * @param driver,data
     */
    public void searchandelete(WebDriver driver, Map<String, String> data) {

        /**
         * search for an Sub category.
         */
        moveMouseAndClick(invoiceSubCatSearch);

        /**
         * click on this sub category.
         */
        moveMouseAndClick(invSubCatSearchChoice);

        /**
         * click on search button.
         */
        moveMouseAndClick(searchBtn);

        /**
         * delete the selected one.
         */
        moveMouseAndClick(deleteRow);

        /**
         * confirm.
         */
        moveMouseAndClick(delete);
        
        WebElement confirm = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirm);

    }

    /**
     * @return the invSubCatSearchChoice
     */
    public WebElement getInvSubCatSearchChoice() {
        return invSubCatSearchChoice;
    }

    /**
     * @param invSubCatSearchChoice the invSubCatSearchChoice to set
     */
    public void setInvSubCatSearchChoice(WebElement invSubCatSearchChoice) {
        this.invSubCatSearchChoice = invSubCatSearchChoice;
    }

    /**
     * @return the descriptionInvSubCat
     */
    public WebElement getDescriptionInvSubCat() {
        return descriptionInvSubCat;
    }

    /**
     * @param descriptionInvSubCat the descriptionInvSubCat to set
     */
    public void setDescriptionInvSubCat(WebElement descriptionInvSubCat) {
        this.descriptionInvSubCat = descriptionInvSubCat;
    }

    /**
     * @return the invSubCatChoice
     */
    public WebElement getInvSubCatChoice() {
        return invSubCatChoice;
    }

    /**
     * @param invSubCatChoice the invSubCatChoice to set
     */
    public void setInvSubCatChoice(WebElement invSubCatChoice) {
        this.invSubCatChoice = invSubCatChoice;
    }

    /**
     * @return the invoiceSubCat
     */
    public WebElement getInvoiceSubCat() {
        return invoiceSubCat;
    }

    /**
     * @param invoiceSubCat the invoiceSubCat to set
     */
    public void setInvoiceSubCat(WebElement invoiceSubCat) {
        this.invoiceSubCat = invoiceSubCat;
    }

    /**
     * @return the invoiceSubCatCode
     */
    public WebElement getInvoiceSubCatCode() {
        return invoiceSubCatCode;
    }

    /**
     * @param invoiceSubCatCode the invoiceSubCatCode to set
     */
    public void setInvoiceSubCatCode(WebElement invoiceSubCatCode) {
        this.invoiceSubCatCode = invoiceSubCatCode;
    }

    /**
     * @return the codeComptable
     */
    public WebElement getCodeComptable() {
        return codeComptable;
    }

    /**
     * @param codeComptable the codeComptable to set
     */
    public void setCodeComptable(WebElement codeComptable) {
        this.codeComptable = codeComptable;
    }

    /**
     * @return the codeComptableChoice
     */
    public WebElement getCodeComptableChoice() {
        return codeComptableChoice;
    }

    /**
     * @param codeComptableChoice the codeComptableChoice to set
     */
    public void setCodeComptableChoice(WebElement codeComptableChoice) {
        this.codeComptableChoice = codeComptableChoice;
    }

    /**
     * @return the descriptionInvSub
     */
    public WebElement getDescriptionInvSub() {
        return descriptionInvSub;
    }

    /**
     * @param descriptionInvSub the descriptionInvSub to set
     */
    public void setDescriptionInvSub(WebElement descriptionInvSub) {
        this.descriptionInvSub = descriptionInvSub;
    }

    /**
     * @return the englishInvSubCat
     */
    public WebElement getEnglishInvSubCat() {
        return englishInvSubCat;
    }

    /**
     * @param englishInvSubCat the englishInvSubCat to set
     */
    public void setEnglishInvSubCat(WebElement englishInvSubCat) {
        this.englishInvSubCat = englishInvSubCat;
    }

    /**
     * @return the frenchshInvSubCat
     */
    public WebElement getFrenchshInvSubCat() {
        return frenchshInvSubCat;
    }

    /**
     * @param frenchshInvSubCat the frenchshInvSubCat to set
     */
    public void setFrenchshInvSubCat(WebElement frenchshInvSubCat) {
        this.frenchshInvSubCat = frenchshInvSubCat;
    }

    /**
     * @return the invoiceSubCatChoiceSearch
     */
    public WebElement getInvoiceSubCatChoiceSearch() {
        return invoiceSubCatChoiceSearch;
    }

    /**
     * @param invoiceSubCatChoiceSearch the invoiceSubCatChoiceSearch to set
     */
    public void setInvoiceSubCatChoiceSearch(WebElement invoiceSubCatChoiceSearch) {
        this.invoiceSubCatChoiceSearch = invoiceSubCatChoiceSearch;
    }

    /**
     * @return the searchBtn
     */
    public WebElement getSearchBtn() {
        return searchBtn;
    }

    /**
     * @param searchBtn the searchBtn to set
     */
    public void setSearchBtn(WebElement searchBtn) {
        this.searchBtn = searchBtn;
    }

    /**
     * @return the invoiceSubCatSearch
     */
    public WebElement getInvoiceSubCatSearch() {
        return invoiceSubCatSearch;
    }

    /**
     * @param invoiceSubCatSearch the invoiceSubCatSearch to set
     */
    public void setInvoiceSubCatSearch(WebElement invoiceSubCatSearch) {
        this.invoiceSubCatSearch = invoiceSubCatSearch;
    }

    /**
     * @return the deleteRow
     */
    public WebElement getDeleteRow() {
        return deleteRow;
    }

    /**
     * @param deleteRow the deleteRow to set
     */
    public void setDeleteRow(WebElement deleteRow) {
        this.deleteRow = deleteRow;
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
}