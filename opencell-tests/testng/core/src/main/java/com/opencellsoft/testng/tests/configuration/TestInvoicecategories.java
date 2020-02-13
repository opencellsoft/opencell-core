package com.opencellsoft.testng.tests.configuration;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.configuration.InvoiceCategoriesPage;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * 
 * @author Hassnaa MIFTAH.
 *
 */
public class TestInvoicecategories extends TestBase {
    /**
     * genreate random values method
     */
    public TestInvoicecategories() {
        String test = "CE_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        data.put(Constants.INVOICE_CATEGORIES_FRENSH, test);
        data.put(Constants.INVOICE_CATEGORIES_ENGLISH, test);
    }
    
    /**
     * test Invoice page method .
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

    }
}
