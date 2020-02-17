package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * @author Edward P. Legaspi
 *
 */
public class InvoiceTypeDetailPage extends BasePage {

    @FindBy(id = "menu:invoiceTypes")
    public WebElement menuBtnInvoiceType;

    @FindBy(id = "searchForm:buttonNew")
    public WebElement btnNew;

    @FindBy(id = "formInvoiceType:code_txt")
    public WebElement codeIpt;

    @FindBy(id = "formInvoiceType:description")
    public WebElement descriptionIpt;

    @FindBy(id = "formInvoiceType:billingTemplateName_txt")
    public WebElement billingTemplateNameIpt;

    @FindBy(id = "formInvoiceType:billingTemplateNameEL_txt")
    public WebElement billingTemplateNameELIpt;

    @FindBy(id = "formInvoiceType:prefix")
    public WebElement prefixIpt;

    @FindBy(id = "formInvoiceType:sequenceSize_input")
    public WebElement sequenceSizeIpt;

    @FindBy(id = "formInvoiceType:currentInvoiceNb_input")
    public WebElement currentInvoiceNbIpt;

    @FindBy(id = "formInvoiceType:pdfFilenameEL_txt")
    public WebElement pdfFilenameELIpt;

    @FindBy(id = "formInvoiceType:xmlFilenameEL_txt")
    public WebElement xmlFilenameELIpt;

    @FindBy(id = "formInvoiceType:occTemplateNegativeSelectedId_text")
    public WebElement occTemplateNegativeSelectedIdIpt;

    @FindBy(id = "formInvoiceType:occTemplateSelectedId_text")
    public WebElement acctOpIpt;

    @FindBy(id = "formInvoiceType:occTemplateNegativeSelectedId_text")
    public WebElement acctOpNegativeInvoiceIpt;

    @FindBy(id = "formInvoiceType:occTemplateSelectedId_selectLink")
    public WebElement occTemplateBtn;

    @FindBy(id = "formInvoiceType:occTemplateNegativeSelectedId_selectLink")
    public WebElement occTemplateNegativeInvoiceBtn;

    @FindBy(id = "formInvoiceType:appliesTo")
    public WebElement appliesToPl;

    @FindBy(id = "formInvoiceType:appliesTo_source")
    public WebElement appliesToSourcePlSelect;

    public InvoiceTypeDetailPage(WebDriver driver) {
        super(driver);
    }

    /**
     * @param driver web driver.
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        Actions action = new Actions(driver);
        action.moveToElement(configurationMenu).build().perform();

        WebElement billingMenu = driver.findElement(By.id("menu:invoicingconfig"));
        action = new Actions(driver);
        action.moveToElement(billingMenu).build().perform();

        menuBtnInvoiceType.click();
    }
    
    /**
     * @param driver web driver
     * @param data mapping data
     */
    public void fillFormAndSave(WebDriver driver, Map<String, String> data) {
        btnNew.click();
        codeIpt.click();
        codeIpt.clear();
        codeIpt.sendKeys((String) data.get(Constants.CODE));

        descriptionIpt.click();
        descriptionIpt.clear();
        descriptionIpt.sendKeys((String) data.get(Constants.DESCRIPTION));

        billingTemplateNameIpt.click();
        billingTemplateNameIpt.clear();
        billingTemplateNameIpt.sendKeys((String) data.get(Constants.INVOICE_TEMPLATE_NAME));

        billingTemplateNameELIpt.click();
        billingTemplateNameELIpt.clear();
        billingTemplateNameELIpt.sendKeys((String) data.get(Constants.INVOICE_TEMPLATE_EL));

        prefixIpt.click();
        prefixIpt.clear();
        prefixIpt.sendKeys((String) data.get(Constants.PREFIX));

        sequenceSizeIpt.click();
        sequenceSizeIpt.clear();
        sequenceSizeIpt.sendKeys((String) data.get(Constants.SEQUENCE_SIZE));

        currentInvoiceNbIpt.click();
        currentInvoiceNbIpt.clear();
        currentInvoiceNbIpt.sendKeys((String) data.get(Constants.CURRENT_INVOICE_NUMBER));

        xmlFilenameELIpt.click();
        xmlFilenameELIpt.clear();
        xmlFilenameELIpt.sendKeys((String) data.get(Constants.XML_FILENAME));

        pdfFilenameELIpt.click();
        pdfFilenameELIpt.clear();
        pdfFilenameELIpt.sendKeys((String) data.get(Constants.PDF_FILENAME));

        occTemplateBtn.click();
        WebElement rgChq = driver.findElement(By.cssSelector("tr.ui-widget-content:nth-child(3) > td:nth-child(1)"));

       // WebElement rgChq = driver.findElements(By.className("ui-datatable-selectable")).get(0);
        rgChq.click();

        occTemplateNegativeInvoiceBtn.click();
        WebElement popupOccTemplateNegativeInvoice = driver.findElement(By.id("searchOccTemplateNegativePopuppopupForm:searchOccTemplateNegativePopupdatatable_data"));
        WebElement rbPlvt = popupOccTemplateNegativeInvoice.findElement(By.className("tr.ui-widget-content:nth-child(3) > td:nth-child(1)"));

        //WebElement rbPlvt = popupOccTemplateNegativeInvoice.findElements(By.className("ui-datatable-selectable")).get(2);
        rbPlvt.click();

        WebElement parentElement = driver.findElement(By.id("formInvoiceType:appliesTo"));
        WebElement targetElement = parentElement.findElements(By.tagName("li")).get(0);
        targetElement.click();

        // WebElement addButtonElement = parentElement.findElement(By.id("formInvoiceType:formButtonsCC:saveButton"));
        // addButtonElement.click();
        
        
    }

}
