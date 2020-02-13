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
 * @author Hassnaa MIFTAH
 */

public class InvoiceCategoriesPage extends BasePage {
    
    /**
     * code of a new invoice categories.
     */
    @FindBy(id = "invoiceCatFormId:tabView:code_txt")
    private WebElement codeInvCat;
    
    /**
     * description.
     */
    @FindBy(id = "invoiceCatFormId:tabView:description")
    private WebElement descriptionInvCat;
    
    /**
     * english field.
     */
    private WebElement englishInvCat;
    
    /**
     * frensh field.
     */
    private WebElement frenchshInvCat;
    /**
     * search button.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBttnInvCat;
    
    /**
     * code to search.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeSearch;
    
    /**
     * invoice category without a subcategory.
     */
    @FindBy(id = "datatable_results:0:code_id_message_link")
    private WebElement deleteRow;
    
    /**
     * invoice category with a sub category.
     */
    @FindBy(id = "datatable_results:0:code_id_message_link")
    private WebElement referringInvoice;
    
    /**
     * delete button.
     */
    @FindBy(id = "invoiceCatFormId:formButtonsCC:deletelink")
    private WebElement delete;
    
    /**
     * confirm button.
     */
    private WebElement confirm;
    
    /**
     * constructor.
     * 
     * @param driver
     */
    public InvoiceCategoriesPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * click on configuration -> invoice categories.
     * 
     * @param driver web driver.
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);
        
        WebElement invConfigMenu = driver.findElement(By.id("menu:invoicingconfig"));
        moveMouse(invConfigMenu);
        
        WebElement invCatMenu = driver.findElement(By.id("menu:invoiceCategories"));
        moveMouseAndClick(invCatMenu);
        
    }
    
    /**
     * go to new page.
     * 
     * @param driver
     */
    
    public void gotoNewPage(WebDriver driver) {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
    }
    
    /**
     * fillFormCreate.
     * 
     * @param driver,data
     * @throws InterruptedException
     */
    
    public void fillFormCreate(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        /**
         * give the code of the invoice categories.
         */
        moveMouseAndClick(codeInvCat);
        codeInvCat.clear();
        codeInvCat.sendKeys((String) data.get(Constants.CODE));
        /**
         * give the description of the invoice categories.
         */
        moveMouseAndClick(descriptionInvCat);
        descriptionInvCat.clear();
        descriptionInvCat.sendKeys((String) data.get(Constants.DESCRIPTION));
        /**
         * give the english description.
         */
        
        englishInvCat = driver.findElements(By.tagName("input")).get(8);
        moveMouseAndClick(englishInvCat);
        englishInvCat.clear();
        englishInvCat.sendKeys((String) data.get(Constants.INVOICE_CATEGORIES_ENGLISH));
        /**
         * give the frensh description.
         */
        
        frenchshInvCat = driver.findElements(By.tagName("input")).get(9);
        moveMouseAndClick(frenchshInvCat);
        frenchshInvCat.clear();
        frenchshInvCat.sendKeys((String) data.get(Constants.INVOICE_CATEGORIES_FRENSH));
        
        WebElement saveBttnInvCat = driver
            .findElement(By.id("invoiceCatFormId:formButtonsCC:saveButton"));
        moveMouseAndClick(saveBttnInvCat);
        /**
         * find an invoice categories with a subcategory.
         */
        moveMouseAndClick(codeSearch);
        codeSearch.clear();
        codeSearch.sendKeys((String) data.get(Constants.CODE));
        /**
         * click on search
         */
        moveMouseAndClick(searchBttnInvCat);
        /**
         * select an element with subcategory.
         */
        moveMouseAndClick(referringInvoice);
        
        /**
         * click on delete.
         */
        moveMouseAndClick(delete);
        /**
         * click on confirm.
         */
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
    }
    
    /**
     * @return the codeInvCat
     */
    public WebElement getCodeInvCat() {
        return codeInvCat;
    }
    
    /**
     * @param codeInvCat the codeInvCat to set
     */
    public void setCodeInvCat(WebElement codeInvCat) {
        this.codeInvCat = codeInvCat;
    }
    
    /**
     * @return the descriptionInvCat
     */
    public WebElement getDescriptionInvCat() {
        return descriptionInvCat;
    }
    
    /**
     * @param descriptionInvCat the descriptionInvCat to set
     */
    public void setDescriptionInvCat(WebElement descriptionInvCat) {
        this.descriptionInvCat = descriptionInvCat;
    }
    
    /**
     * @return the englishInvCat
     */
    public WebElement getEnglishInvCat() {
        return englishInvCat;
    }
    
    /**
     * @param englishInvCat the englishInvCat to set
     */
    public void setEnglishInvCat(WebElement englishInvCat) {
        this.englishInvCat = englishInvCat;
    }
    
    /**
     * @return the frenchshInvCat
     */
    public WebElement getFrenchshInvCat() {
        return frenchshInvCat;
    }
    
    /**
     * @param frenchshInvCat the frenchshInvCat to set
     */
    public void setFrenchshInvCat(WebElement frenchshInvCat) {
        this.frenchshInvCat = frenchshInvCat;
    }
    
    /**
     * @return the searchBttnInvCat
     */
    public WebElement getSearchBttnInvCat() {
        return searchBttnInvCat;
    }
    
    /**
     * @param searchBttnInvCat the searchBttnInvCat to set
     */
    public void setSearchBttnInvCat(WebElement searchBttnInvCat) {
        this.searchBttnInvCat = searchBttnInvCat;
    }
    
    /**
     * @return the codeSearch
     */
    public WebElement getCodeSearch() {
        return codeSearch;
    }
    
    /**
     * @param codeSearch the codeSearch to set
     */
    public void setCodeSearch(WebElement codeSearch) {
        this.codeSearch = codeSearch;
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
     * @return the referringInvoice
     */
    public WebElement getReferringInvoice() {
        return referringInvoice;
    }
    
    /**
     * @param referringInvoice the referringInvoice to set
     */
    public void setReferringInvoice(WebElement referringInvoice) {
        this.referringInvoice = referringInvoice;
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
     * @return the confirm
     */
    public WebElement getConfirm() {
        return confirm;
    }
    
    /**
     * @param confirm the confirm to set
     */
    public void setConfirm(WebElement confirm) {
        this.confirm = confirm;
    }
}
