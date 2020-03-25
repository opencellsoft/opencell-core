package com.opencellsoft.testng.tests.setup;

import static org.testng.Assert.assertEquals;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.setup.InvoiceSequencePage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestInvoiceSequence extends TestBase  {
	 /**
     * fill the constants.
     */
    public TestInvoiceSequence() {
        String test = "Seq_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);        
    }
	  @Test
	    private void testBillingAccount() throws InterruptedException {
		  InvoiceSequencePage invoiceSequencePage = PageFactory.initElements(this.getDriver(), InvoiceSequencePage.class);
		  invoiceSequencePage.gotoListPage(driver);
		  invoiceSequencePage.fillFormInvSeq(driver,data);
		  testData(invoiceSequencePage);
		  invoiceSequencePage.saveInvSeq(driver);
		  invoiceSequencePage.searchInvSeq(driver, data);
		  invoiceSequencePage.deleteInvSeq(driver, data);
	    }
	  private void testData(InvoiceSequencePage invoiceSequencePage) {
			String code = invoiceSequencePage.getCodeInvSeq().getAttribute(ATTRIBUTE_VALUE);
			assertEquals(code, data.get(Constants.CODE));
		}

}
