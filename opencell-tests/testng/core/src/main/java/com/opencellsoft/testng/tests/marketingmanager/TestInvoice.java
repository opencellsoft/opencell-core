package com.opencellsoft.testng.tests.marketingmanager;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.marketingmanager.InvoicePage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestInvoice extends TestBase {
    /**
     * test Invoice Page.
     * 
     * @throws InterruptedException Exception
     */
    @Test
    public void testInvoicePage() throws InterruptedException {
        /**
         * init test .
         */
        InvoicePage invoicePage = PageFactory.initElements(this.getDriver(), InvoicePage.class);
        Thread.sleep(2000);
        invoicePage.billing(driver);
    }
}
