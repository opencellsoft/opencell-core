package com.opencellsoft.testng.tests.configuration;

import static org.testng.Assert.assertEquals;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.configuration.InvoiceTypesPage;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;

import org.testng.annotations.Test;

/**
 * 
 * @author Maria AIT BRAHIM
 *
 */
public class TestInvoiceTypes extends TestBase {

    /**
     * fill the constants.
     */
    public TestInvoiceTypes() {
        String test = "RE_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        data.put(Constants.INVOICE_TEMPLATE_NAME, test);
        data.put(Constants.INVOICE_TEMPLATE_EL, test);
        data.put(Constants.PREFIX, test);
        data.put(Constants.PDF_FILENAME, test);
        data.put(Constants.XML_FILENAME, test);

    }

    /**
     * invoice Types Test.
     * @throws InterruptedException 
     */


    @Test
    private void testInvoiceTypes() throws InterruptedException {
        InvoiceTypesPage invoiceTypesPage = PageFactory.initElements(this.getDriver(),
            InvoiceTypesPage.class);
        invoiceTypesPage.gotoListPage(driver);
        invoiceTypesPage.gotoNewPage(driver);
        invoiceTypesPage.fillFormCreate(driver, data);
      
    }

    /**
     * Check the mandatory fields.
     * 
     * @param page instance of InvoiceTypes
     */
/*
    private void testData(InvoiceTypesPage page) {
        String code = page.getCodeIt().getAttribute(ATTRIBUTE_VALUE);
        String description = page.getDescriptionIt().getAttribute(ATTRIBUTE_VALUE);
        String invoiceTemplateNname = page.getInvoiceTemplateName().getAttribute(ATTRIBUTE_VALUE);
        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));
        assertEquals(invoiceTemplateNname, data.get(Constants.INVOICE_TEMPLATE_NAME));

        String templateNameEl = page.getTemplateNameElIt().getAttribute(ATTRIBUTE_VALUE);
        assertEquals(templateNameEl, data.get(Constants.INVOICE_TEMPLATE_EL));

        String prefixIt = page.getPrefixIt().getAttribute(ATTRIBUTE_VALUE);
        String pdfElIt = page.getPdfElIt().getAttribute(ATTRIBUTE_VALUE);
        String xmlElIt = page.getXmlElIt().getAttribute(ATTRIBUTE_VALUE);

        assertEquals(prefixIt, data.get(Constants.PREFIX));
        assertEquals(pdfElIt, data.get(Constants.PDF_FILENAME));
        assertEquals(xmlElIt, data.get(Constants.XML_FILENAME));
*/
   

}
