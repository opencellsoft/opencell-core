package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * @author Hassnaa MIFTAH
 *
 */
public class OneShotPage extends BasePage {
    
    /**
     * code.
     */
    @FindBy(id = "formId:tabView:code_txt")
    private WebElement code;
    
    /**
     * unit Multiplicator Number.
     */
    @FindBy(id = "formId:tabView:unitMultiplicator_number")
    private WebElement unitMultiplicator;
    
    /**
     * rating Unit Description.
     */
    @FindBy(id = "formId:tabView:ratingUnitDescription_txt")
    private WebElement ratingUnitDescription;
    
    /**
     * input Unit Description.
     */
    @FindBy(id = "formId:tabView:inputUnitDescription_txt")
    private WebElement inputUnitDescription;
    /**
     * filter Expression.
     */
    @FindBy(id = "formId:tabView:filterExpression_txt")
    private WebElement filterExpression;
    
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
     * select one Shot Charge Template Type..
     */
    @FindBy(id = "formId:tabView:oneShotChargeTemplateType_enum_2")
    private WebElement selectedOneShotChargeTemplateType;
    
    /**
     * one Shot Charge Template Type.
     */
    @FindBy(id = "formId:tabView:oneShotChargeTemplateType_enum")
    private WebElement oneShotChargeTemplateType;
    
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
     * Invoice immediatly.
     */
    @FindBy(id = "formId:tabView:immediateInvoicing_bool")
    private WebElement InvoiceImmediatly;
    /**
     * Triggered EDR.
     */
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/div/div/div[14]/div/div/div[1]/ul/li[1]\r\n")
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
    public OneShotPage(final WebDriver driver) {
        super(driver);
        
    }
    
    /**
     * click on catalog -> service management -> charges -> OneShot.
     * 
     * @param driver WebDriver
     * 
     */
    public void gotoListPage(final WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:catalog"));
        moveMouse(configurationMenu);
        
        WebElement serviceMenu = driver.findElement(By.id("menu:serviceManagement"));
        moveMouse(serviceMenu);
        
        WebElement chargesMenu = driver.findElement(By.id("menu:charges"));
        moveMouse(chargesMenu);
        
        WebElement oneShotMenu = driver.findElement(By.id("menu:oneShotChargeTemplates"));
        moveMouseAndClick(oneShotMenu);
        
    }
    
    /**
     * the button save.
     * 
     * @param driver WebDriver
     */
    public void save(WebDriver driver) {
        bttnSave.click();
    }
    
    /**
     * fill the new One shot.
     * 
     * @param driver instance of WebDriver
     * @param data Map
     */
    
    public void fillFormCreate(WebDriver driver, Map<String, String> data) {
        // Code
        bttnNew.click();
        getCode().click();
        getCode().clear();
        getCode().sendKeys((String) data.get(Constants.CODE));
        
        // unitMultiplicator
        unitMultiplicator.click();
        unitMultiplicator.sendKeys("1");
        
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
        
        // one Shot Charge Template Type
        oneShotChargeTemplateType.click();
        selectedOneShotChargeTemplateType.click();
        
        // SubCategory
        invoiceSubCategory.click();
        invoiceSubCategorySelectedId.click();
        
        // filter Expression
        filterExpression.click();
        filterExpression.sendKeys(
            " <filter><disabled>false</disabled><appendGeneratedCode>false</appendGeneratedCode>\\r\\n\" + \"     <filterCondition class=\\\"andCompositeFilterCondition\\\">\\r\\n\"\r\n"
                    + "        + \"<filterConditionType>COMPOSITE_AND</filterConditionType>\\r\\n\" + \"<filterConditions>\\r\\n\" + \"<primitiveFilterCondition>\\r\\n\"\r\n"
                    + "        + \"");
        
        // Invoice Immediatly
        InvoiceImmediatly.click();
        
        // amount Editable
        
        amountEditable.click();
        
        // Triggered EDR
        triggeredEdr.click();
        WebElement iconLeft = driver.findElements(By.className("ui-button-icon-left")).get(0);
        iconLeft.click();
        bttnSave.click();
        deleteBttn.click();
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        confirmDelete.click();
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
