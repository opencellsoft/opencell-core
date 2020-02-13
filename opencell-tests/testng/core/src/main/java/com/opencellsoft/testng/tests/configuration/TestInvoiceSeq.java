package com.opencellsoft.testng.tests.configuration;

import com.opencellsoft.testng.tests.base.TestBase;
import com.opencellsoft.testng.pages.configuration.InvoiceSepPage;
import com.opencellsoft.testng.pages.Constants;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

public class TestInvoiceSeq extends TestBase {

	public TestInvoiceSeq() {
		String test = "RE_" + System.currentTimeMillis();
		data.put(Constants.CODE, test);
	}

	@Test
	private void testInvoiceSeq() throws InterruptedException {
		InvoiceSepPage testInvoiceSeq = PageFactory.initElements(this.getDriver(), InvoiceSepPage.class);
		testInvoiceSeq.gotoListPage(driver);
		testInvoiceSeq.goTobtnNew(driver);
		testInvoiceSeq.fillData(driver, data);
	}
}
