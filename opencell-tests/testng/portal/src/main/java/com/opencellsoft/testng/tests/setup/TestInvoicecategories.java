package com.opencellsoft.testng.tests.setup;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.setup.InvoiceCategoriesPage;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * 
 * @author Maria AIT BRAHIM.
 *
 */
public class TestInvoicecategories extends TestBase {
    /**
     * generate random values method
     */
    public TestInvoicecategories() {
        String test = "IC_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        
    }
    
    /**
     * test Invoice page method .
     * 
     * @throws InterruptedException
     */
    @Test
    private void testInvoiceCategoriesPage() throws InterruptedException {
        InvoiceCategoriesPage invoiceCategoriesPage = PageFactory.initElements(this.getDriver(),
            InvoiceCategoriesPage.class);
        invoiceCategoriesPage.gotoListPage(driver);
        /**
         * method to click on new and go to next page
         */
        invoiceCategoriesPage.gotoNewPage(driver);
        /**
         * method to fill the form
         */
        invoiceCategoriesPage.fillFormCreate(driver, data);
        
        // Searching and deleting data
        invoiceCategoriesPage.fillAndSearch(driver, data);
        
    }
}
