package com.opencellsoft.testng.tests.setup;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.setup.InvoiceSubCategoriesPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Maria AIT BRAHIM.
 */
public class TestInvoiceSubCategories extends TestBase {
    
    /**
     * genreate random values method
     */
    public TestInvoiceSubCategories() {
        String test = "CE_" + System.currentTimeMillis();
        
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        
        
    }
    
    /**
     * test Invoice method .
     * 
     * @throws InterruptedException
     */
    @Test
    private void testInvoiceSubCategoriesPage() throws InterruptedException {
        
        InvoiceSubCategoriesPage invoiceSubCategoriesPage = PageFactory
            .initElements(this.getDriver(), InvoiceSubCategoriesPage.class);
        invoiceSubCategoriesPage.gotoListPage(driver);
        invoiceSubCategoriesPage.gotoNewPage(driver);
        invoiceSubCategoriesPage.fillFormCreate(driver, data);
        invoiceSubCategoriesPage.saveInvCat(driver);
       invoiceSubCategoriesPage.searchandelete(driver, data);
        
    }
}
