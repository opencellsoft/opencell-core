package com.opencellsoft.testng.tests.catalog;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.InvoiceTypeDetailPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author Edward P. Legaspi
 * 
 */
public class TestInvoiceTypeCreate extends TestBase {

    /**
     * default constructor.
     */
    public TestInvoiceTypeCreate() {
        String test = "IT_" + System.currentTimeMillis();
        String sequenceSize = "7", currentInvoiceNo = "1";

        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        data.put(Constants.INVOICE_TEMPLATE_NAME, test);
        data.put(Constants.INVOICE_TEMPLATE_EL, test + "_#{invoice.id}");
        data.put(Constants.PREFIX, test);
        data.put(Constants.SEQUENCE_SIZE, sequenceSize);
        data.put(Constants.CURRENT_INVOICE_NUMBER, currentInvoiceNo);
        data.put(Constants.PDF_FILENAME, test);
        data.put(Constants.XML_FILENAME, test);
        data.put(Constants.ACCOUNT_OPERATION, "RG_CHQ");
        data.put(Constants.ACCOUNT_OPERATION_NEGATIVE_INVOICE, "RB_PLVT");
    }

    /**
     * test creation.
     */
    @Test
    private void testCreate()throws InterruptedException {
        InvoiceTypeDetailPage detailPage = PageFactory.initElements(this.getDriver(), InvoiceTypeDetailPage.class);
        detailPage.gotoListPage(driver);
        detailPage.fillFormAndSave(driver, data);
    }

    /**
     * test input data.
     */
    private void testData() {
        InvoiceTypeDetailPage newEntity = PageFactory.initElements(this.getDriver(), InvoiceTypeDetailPage.class);

        String code = newEntity.codeIpt.getAttribute(ATTRIBUTE_VALUE);
        String description = newEntity.descriptionIpt.getAttribute(ATTRIBUTE_VALUE);
        String invoiceTemplateName = newEntity.billingTemplateNameIpt.getAttribute(ATTRIBUTE_VALUE);
        String invoiceTemplateNameEL = newEntity.billingTemplateNameELIpt.getAttribute(ATTRIBUTE_VALUE);
        String prefix = newEntity.prefixIpt.getAttribute(ATTRIBUTE_VALUE);
        String sequenceSize = newEntity.sequenceSizeIpt.getAttribute(ATTRIBUTE_VALUE);
        String currentInvoiceNo = newEntity.currentInvoiceNbIpt.getAttribute(ATTRIBUTE_VALUE);
        String pdfFilename = newEntity.pdfFilenameELIpt.getAttribute(ATTRIBUTE_VALUE);
        String xmlFilename = newEntity.xmlFilenameELIpt.getAttribute(ATTRIBUTE_VALUE);
        String acctOp = newEntity.acctOpIpt.getAttribute(ATTRIBUTE_VALUE);
        String acctOpNegInvoice = newEntity.acctOpNegativeInvoiceIpt.getAttribute(ATTRIBUTE_VALUE);

        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));
        assertEquals(invoiceTemplateName, data.get(Constants.INVOICE_TEMPLATE_NAME));
        assertEquals(invoiceTemplateNameEL, data.get(Constants.INVOICE_TEMPLATE_EL));
        assertEquals(prefix, data.get(Constants.PREFIX));
        assertEquals(sequenceSize, data.get(Constants.SEQUENCE_SIZE));
        assertEquals(currentInvoiceNo, data.get(Constants.CURRENT_INVOICE_NUMBER));
        assertEquals(pdfFilename, data.get(Constants.PDF_FILENAME));
        assertEquals(xmlFilename, data.get(Constants.XML_FILENAME));
        assertEquals(acctOp, data.get(Constants.ACCOUNT_OPERATION));
        assertEquals(acctOpNegInvoice, data.get(Constants.ACCOUNT_OPERATION_NEGATIVE_INVOICE));
    }

}
