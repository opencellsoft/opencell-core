package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.Constants;

/**
 * @author Edward P. Legaspi
 * @created 15 Jan 2018
 **/
public class OneshotChargeDetailPage extends ChargeTemplatePage {


    @FindBy(id = "formId:tabView:oneShotChargeTemplateType_enum_2")
    public WebElement oneshotChargeTemplateTypeIpt;

    @FindBy(id = "formId:tabView:description")
    public WebElement descriptionIpt;



    @FindBy(id = "formId:tabView:ratingUnitDescription_txt")
    public WebElement ratingDescriptionIpt;

    @FindBy(id = "formId:tabView:unitMultiplicator_number")
    public WebElement unitMultiplicatorIpt;

    @FindBy(id = "formId:tabView:unitNbDecimal_number_input")
    public WebElement unitNbDecimalIpt;

    @FindBy(id = "formId:tabView:filterExpression_txt")
    public WebElement filterExpressionIpt;

    @FindBy(id = "formId:tabView:amountEditable_bool")
    public WebElement amountEditableIpt;

    @FindBy(id = "formId:tabView:roundingMode_enum")
    public WebElement roundingModeDropdownIpt;

    @FindBy(id = "formId:tabView:roundingMode_enum_3")
    public WebElement roundingModeDropdownUpIpt;

    @FindBy(id = "formId:tabView:invoiceSubCategorySelectedId")
    public WebElement invoiceSubCategorySelectionIpt;

    @FindBy(id = "formId:tabView:invoiceSubCategorySelectedId_1")
    public WebElement invoiceSubCategoryConsumptionIpt;
    
    @FindBy(id="formId:formButtonsCC:saveButtonAjax")
    public WebElement saveCharge;
    
    @FindBy(id ="formId:formButtonsCC:backButton")
    public WebElement backBtn;
    
    
    @FindBy(id ="searchForm:code_txt")
    public WebElement searchCode;
    
    @FindBy(id ="searchForm:buttonSearch")
    public WebElement searchBtn;
    
    @FindBy(id="formId:formButtonsCC:deletelink")
    public WebElement deleteBtn;

    /**
     * @param driver web driver.
     */
    public OneshotChargeDetailPage(WebDriver driver) {
        super(driver);
    }

    /**
     * @param driver web driver.
     */
    public void gotoListPage(WebDriver driver) {
        WebElement catalogMenu = driver.findElement(By.id("menu:catalog"));
        moveMouseAndClick(catalogMenu);

        WebElement serviceManagementMenu = driver.findElement(By.id("menu:serviceManagement"));
        moveMouseAndClick(serviceManagementMenu);

        WebElement chargesMenu = driver.findElement(By.id("menu:charges"));
        moveMouseAndClick(chargesMenu);

        WebElement chargeTemplatesMenu = driver.findElement(By.id("menu:oneShotChargeTemplates"));
        moveMouseAndClick(chargeTemplatesMenu);
    }

    /**
     * @param driver web driver.
     * @param data mapping data
     * @throws InterruptedException 
     */
    public void fillFormAndSave(WebDriver driver, Map<String, String> data) throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement btnNew = driver
                    .findElement(By.id("searchForm:buttonNew"));
                moveMouseAndClick(btnNew);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        WebElement codeIpt = driver.findElement(By.id("formId:tabView:code_txt"));
        moveMouseAndClick(codeIpt);
        codeIpt.clear();
        codeIpt.sendKeys((String) data.get(Constants.CODE));
        WebElement oneshotChargeTemplateTypeDropdownIpt = driver.findElement(By.id("formId:tabView:oneShotChargeTemplateType_enum"));
        oneshotChargeTemplateTypeDropdownIpt.click();
        oneshotChargeTemplateTypeIpt.click();
        WebElement inputDescriptionIpt = driver.findElement(By.id("formId:tabView:inputUnitDescription_txt"));
        moveMouseAndClick(inputDescriptionIpt);
        inputDescriptionIpt.clear();
        inputDescriptionIpt.sendKeys((String) data.get(Constants.INPUT_DESCRIPTION));
        moveMouseAndClick(ratingDescriptionIpt);
        ratingDescriptionIpt.clear();
        ratingDescriptionIpt.sendKeys((String) data.get(Constants.RATING_DESCRIPTION));
        moveMouseAndClick(unitMultiplicatorIpt);
        moveMouseAndClick(unitMultiplicatorIpt);
        unitMultiplicatorIpt.sendKeys((String) data.get(Constants.UNIT_MULTIPLICATOR));
        moveMouseAndClick(unitNbDecimalIpt);
        unitNbDecimalIpt.clear();
        unitNbDecimalIpt.sendKeys((String) data.get(Constants.UNIT_NB_DECIMAL));
        roundingModeDropdownIpt.click();
        roundingModeDropdownUpIpt.click();
        moveMouseAndClick(filterExpressionIpt);
        filterExpressionIpt.clear();
        filterExpressionIpt.sendKeys((String) data.get(Constants.FILTER_EXPRESSION));
        invoiceSubCategorySelectionIpt.click();
        invoiceSubCategoryConsumptionIpt.click();
        moveMouseAndClick(descriptionIpt);
        descriptionIpt.clear();
        descriptionIpt.sendKeys((String) data.get(Constants.DESCRIPTION));
        amountEditableIpt.click();
        moveMouseAndClick(saveCharge);
        moveMouseAndClick(backBtn);
        moveMouseAndClick(searchCode);
        searchCode.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(searchBtn);
        
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement deleteRow = driver
                    .findElement(By.id("datatable_results:0:code_id_message_link"));
                moveMouseAndClick(deleteRow);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        moveMouseAndClick(deleteBtn);
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        confirmDelete.click();
        
        
    }

}
