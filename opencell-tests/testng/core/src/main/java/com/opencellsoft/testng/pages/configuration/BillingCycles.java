package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
/**
 * Billing Cycles page.
 * 
 * @author Fatine BELHADJ.
 * 
 * 
 *
 */
public class BillingCycles extends BasePage {
    /**
     * button new.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement btnNew;
    /**
     * new code label.
     */
    @FindBy(id = "formBillingCycle:tabView:code_txt")
    private WebElement codeBilling;
    /**
     * new description label.
     */
    @FindBy(id = "formBillingCycle:tabView:description")
    private WebElement descriptionBilling;
    /**
     * billing template name.
     */
    @FindBy(id = "formBillingCycle:tabView:billingTemplateNameEL_txt")
    private WebElement billingTemplateName;
    /**
     * due date deklay.
     */
    @FindBy(id = "formBillingCycle:tabView:dueDateDelayEL_txt")
    private WebElement dueDateDelayel;
    /**
     * delai derniere transaction.
     */
    @FindBy(id = "formBillingCycle:tabView:transactionDateDelay_number_input")
    private WebElement delaiDerniereTransaction;
    /**
     * delai facture immediate.
     */
    @FindBy(id = " ui-icon-triangle-1-s")
    private WebElement delaiFactuImmediate;
    /**
     * delai date facture.
     */
    @FindBy(id = "formBillingCycle:tabView:invoiceDateProductionDelay_number_input")
    private WebElement delaiDateFacture;
    
    /**
     * button to choose from calender.
     */
    @FindBy(id = "formBillingCycle:tabView:calendar_entity_1")
    private WebElement calenderChoose;
    /**
     * type facture.
     */
    @FindBy(id = "formBillingCycle:tabView:invoiceTypeSelectedId_selectLink")
    private WebElement typeFacture;
    
    /**
     * due day delay.
     */
    @FindBy(id = "formBillingCycle:tabView:dueDateDelay_number_input")
    private WebElement dueDateDelay;
    /**
     * button save.
     */
    @FindBy(id = "formBillingCycle:formButtonsCC:saveButton")
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
     * button search.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement search;
    /**
     * button delete.
     */
    @FindBy(id = "datatable_results:0:resultsdeletelink")
    private WebElement btnDelete;
    
    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public BillingCycles(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Opening billing cycle page.
     * 
     * @param driver billing cycle
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);
        
        WebElement facturation = driver.findElement(By.id("menu:invoicingconfig"));
        moveMouse(facturation);
        
        WebElement billingCycles = driver.findElement(By.id("menu:billingCycles"));
        moveMouseAndClick(billingCycles);
    }
    
    /**
     * clicking on new.
     * 
     * @param driver WebDriver
     */
    public void goTobtnNew(WebDriver driver) {
        moveMouseAndClick(btnNew);
    }
    
    /**
     * Entering data.
     * 
     * @param driver new billing cycle
     * @param data code,description,billing template,
     * @throws InterruptedException
     */
    public void fillData(WebDriver driver, Map<String, String> data) throws InterruptedException {
        moveMouseAndClick(codeBilling);
        codeBilling.clear();
        codeBilling.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(descriptionBilling);
        descriptionBilling.clear();
        descriptionBilling.sendKeys((String) data.get(Constants.DESCRIPTION));
        moveMouseAndClick(billingTemplateName);
        billingTemplateName.clear();
        billingTemplateName.sendKeys((String) data.get(Constants.DESCRIPTION));
        moveMouseAndClick(dueDateDelayel);
        dueDateDelayel.clear();
        dueDateDelayel.sendKeys((String) data.get(Constants.DESCRIPTION));
        WebElement immediateInvoicingDelay = driver
            .findElements(By.className("ui-icon-triangle-1-n")).get(3);
        moveMouseAndClick(immediateInvoicingDelay);
        moveMouseAndClick(delaiDateFacture);
        delaiDateFacture.sendKeys("0");
        moveMouseAndClick(dueDateDelay);
        dueDateDelay.sendKeys("1");
        WebElement calendarEntity = driver.findElement(By.xpath("/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/div/div/div[4]/div/div/div[3]/span"));
        moveMouseAndClick(calendarEntity);
        WebElement calendarEntity1 = driver.findElement(By.id("formBillingCycle:tabView:calendar_entity_1"));
        moveMouseAndClick(calendarEntity1);
        WebElement TypeBC = driver.findElement(By.id("formBillingCycle:tabView:type_enum_label"));
        moveMouseAndClick(TypeBC);
        WebElement TypeBCSelect = driver.findElement(By.id("formBillingCycle:tabView:type_enum_1"));
        moveMouseAndClick(TypeBCSelect);
        moveMouseAndClick(btnSave);
        moveMouseAndClick(codeSearch);
        codeSearch.clear();
        codeSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(descriptionSearch);
        descriptionSearch.clear();
        descriptionSearch.sendKeys((String) data.get(Constants.DESCRIPTION));
        moveMouseAndClick(search);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement chartToDelete = driver
                    .findElement(By.id("datatable_results:0:code_id_message_link"));
                moveMouseAndClick(chartToDelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        WebElement delete = driver.findElement(By.id("formBillingCycle:formButtonsCC:deletelink"));
        moveMouseAndClick(delete);
        WebElement confirmBtn = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmBtn);
    }
    
}
