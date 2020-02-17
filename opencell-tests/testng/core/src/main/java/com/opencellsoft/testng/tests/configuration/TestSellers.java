package com.opencellsoft.testng.tests.configuration;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.configuration.SellersPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author MIFTAH 
 *
 */
public class TestSellers extends TestBase {
    /**
     * testSeller method.
     * 
     * @throws InterruptedException Exception
     */
    @Test
    public void testSellerPage() throws InterruptedException {  
        /***
         * init test
         */
        SellersPage sellersPage = PageFactory.initElements(this.getDriver(), SellersPage.class);
        /**
         * Go to Sellers page
         */
        sellersPage.gotoListPage(driver);
        /**
         * Fill the new Sellers form seller and information
         */
        sellersPage.fillForm(driver, data);
 
    }
}
