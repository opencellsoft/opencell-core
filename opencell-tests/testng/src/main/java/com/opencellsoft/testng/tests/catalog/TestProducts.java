package com.opencellsoft.testng.tests.catalog;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.ProductsPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author HASSNAA MIFTAH
 *
 */
public class TestProducts extends TestBase {
    /**
     * fill the constants.
     */
    public TestProducts() {
        String test = "PR_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        
    }
    
    /**
     * Test Products Page
     * 
     * @throws InterruptedException
     */
    @Test
    public void createProducts() throws InterruptedException {
        
        /**
         * init test
         */
        ProductsPage productsPage = PageFactory.initElements(this.getDriver(), ProductsPage.class);
        
        /**
         * Go to Products page.
         */
        productsPage.gotoListPage(driver);
        
        /**
         * Fill the New Product Template.
         */
        productsPage.fillForm(driver, data);
    }
}
