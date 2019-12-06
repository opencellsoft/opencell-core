package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * @author Maria AIT BRAHIM
 *
 */
public class ProductChargesPage extends BasePage {
    
    /**
     * @return the inputUnitDescription
     */
    public WebElement getInputUnitDescription() {
        return inputUnitDescription;
    }

    /**
     * @param inputUnitDescription the inputUnitDescription to set
     */
    public void setInputUnitDescription(WebElement inputUnitDescription) {
        this.inputUnitDescription = inputUnitDescription;
    }

    /**
     * @return the ratingUnitDescription
     */
    public WebElement getRatingUnitDescription() {
        return ratingUnitDescription;
    }

    /**
     * @param ratingUnitDescription the ratingUnitDescription to set
     */
    public void setRatingUnitDescription(WebElement ratingUnitDescription) {
        this.ratingUnitDescription = ratingUnitDescription;
    }

    /**
     * @return the unitMultiplicatorNumber
     */
    public WebElement getUnitMultiplicatorNumber() {
        return unitMultiplicatorNumber;
    }

    /**
     * @param unitMultiplicatorNumber the unitMultiplicatorNumber to set
     */
    public void setUnitMultiplicatorNumber(WebElement unitMultiplicatorNumber) {
        this.unitMultiplicatorNumber = unitMultiplicatorNumber;
    }

    /**
     * @return the unitNbDecimal
     */
    public WebElement getUnitNbDecimal() {
        return unitNbDecimal;
    }

    /**
     * @param unitNbDecimal the unitNbDecimal to set
     */
    public void setUnitNbDecimal(WebElement unitNbDecimal) {
        this.unitNbDecimal = unitNbDecimal;
    }

    /**
     * @return the roundingMode
     */
    public WebElement getRoundingMode() {
        return roundingMode;
    }

    /**
     * @param roundingMode the roundingMode to set
     */
    public void setRoundingMode(WebElement roundingMode) {
        this.roundingMode = roundingMode;
    }

    /**
     * @return the invoiceSubCategory
     */
    public WebElement getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    /**
     * @param invoiceSubCategory the invoiceSubCategory to set
     */
    public void setInvoiceSubCategory(WebElement invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    /**
     * @return the invoiceSubCategorySelectedId
     */
    public WebElement getInvoiceSubCategorySelectedId() {
        return invoiceSubCategorySelectedId;
    }

    /**
     * @param invoiceSubCategorySelectedId the invoiceSubCategorySelectedId to set
     */
    public void setInvoiceSubCategorySelectedId(WebElement invoiceSubCategorySelectedId) {
        this.invoiceSubCategorySelectedId = invoiceSubCategorySelectedId;
    }

    /**
     * @return the amountEditable
     */
    public WebElement getAmountEditable() {
        return amountEditable;
    }

    /**
     * @param amountEditable the amountEditable to set
     */
    public void setAmountEditable(WebElement amountEditable) {
        this.amountEditable = amountEditable;
    }

    /**
     * @return the triggeredEdr
     */
    public WebElement getTriggeredEdr() {
        return triggeredEdr;
    }

    /**
     * @param triggeredEdr the triggeredEdr to set
     */
    public void setTriggeredEdr(WebElement triggeredEdr) {
        this.triggeredEdr = triggeredEdr;
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
     * @return the deleteBttn
     */
    public WebElement getDeleteBttn() {
        return deleteBttn;
    }

    /**
     * @param deleteBttn the deleteBttn to set
     */
    public void setDeleteBttn(WebElement deleteBttn) {
        this.deleteBttn = deleteBttn;
    }

    /**
     * @return the bttnSave
     */
    public WebElement getBttnSave() {
        return bttnSave;
    }

    /**
     * @param bttnSave the bttnSave to set
     */
    public void setBttnSave(WebElement bttnSave) {
        this.bttnSave = bttnSave;
    }

    /**
     * @return the bttnNew
     */
    public WebElement getBttnNew() {
        return bttnNew;
    }

    /**
     * @param bttnNew the bttnNew to set
     */
    public void setBttnNew(WebElement bttnNew) {
        this.bttnNew = bttnNew;
    }

    /**
     * input Unit Description.
     */
    @FindBy(id = "formId:tabView:inputUnitDescription_txt")
    private WebElement inputUnitDescription;
    
    /**
     * rating Unit Description.
     */
    @FindBy(id = "formId:tabView:ratingUnitDescription_txt")
    private WebElement ratingUnitDescription;
    
    /**
     * unit Multiplicator number.
     */
    @FindBy(id = "formId:tabView:unitMultiplicator_number")
    private WebElement unitMultiplicatorNumber;
    
    /**
     * description.
     */
    @FindBy(id = "formId:tabView:description")
    private WebElement description;
    
    /**
     * Unit nb decimal.
     */
    @FindBy(id = "formId:tabView:unitNbDecimal_number_input")
    private WebElement unitNbDecimal;
    
    /**
     * rounding Mode.
     */
    @FindBy(id = "formId:tabView:roundingMode_enum")
    private WebElement roundingMode;
    
    /**
     * SubCategory.
     */
    @FindBy(id = "formId:tabView:invoiceSubCategorySelectedId")
    private WebElement invoiceSubCategory;
    
    /**
     * invoice Sub Category Selected Id.
     */
    @FindBy(id = "formId:tabView:invoiceSubCategorySelectedId_1")
    private WebElement invoiceSubCategorySelectedId;
    
    /**
     * amount Editable.
     */
    @FindBy(id = "formId:tabView:amountEditable_bool")
    private WebElement amountEditable;
    
    /**
     * Triggered EDR.
     */
    @FindBy(css = "button.ui-button-icon-only:nth-child(1)")
    private WebElement triggeredEdr;
    
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
     * button save.
     */
    @FindBy(id = "formId:formButtonsCC:saveButtonAjax")
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
    public ProductChargesPage(final WebDriver driver) {
        super(driver);
        
    }
    
    /**
     * click on catalog -> product management -> Product Charges.
     * 
     * @param driver WebDriver
     * 
     */
    public void gotoListPage(final WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:catalog"));
        moveMouse(configurationMenu);
        
        WebElement chargesMenu = driver.findElement(By.id("menu:productManagement"));
        moveMouse(chargesMenu);
        
        WebElement productChargesMenu = driver.findElement(By.id("menu:productChargeTemplates"));
        moveMouseAndClick(productChargesMenu);
        
    }
    
    /**
     * fill the new product Charges.
     * 
     * @param driver instance of WebDriver
     * @param data Map
     */
    
    public void fillFormCreate(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(bttnNew);
        WebElement code = driver.findElement(By.id("formId:tabView:code_txt"));
        // Code
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        
        // unitMultiplicator
        unitMultiplicatorNumber.click();
        unitMultiplicatorNumber.sendKeys("1");
        
        // rating Unit Description
        ratingUnitDescription.click();
        ratingUnitDescription.clear();
        ratingUnitDescription.sendKeys("minutes");
        
        // input Unit Description.
        inputUnitDescription.click();
        inputUnitDescription.clear();
        inputUnitDescription.sendKeys("minutes");
        
        // Unit nb decimal.
        unitNbDecimal.click();
        unitNbDecimal.clear();
        unitNbDecimal.sendKeys("12");
        
        // Description
        getDescription().click();
        getDescription().clear();
        getDescription().sendKeys((String) data.get(Constants.DESCRIPTION));
        
        // SubCategory
        invoiceSubCategory.click();
        invoiceSubCategorySelectedId.click();
        
        // amount Editable
        
        amountEditable.click();
        
        // Triggered EDR
        triggeredEdr.click();
        WebElement iconLeft = driver.findElements(By.className("ui-button-icon-left")).get(0);
        iconLeft.click();
        moveMouseAndClick(bttnSave);
        moveMouseAndClick(deleteBttn);
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        confirmDelete.click();
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
