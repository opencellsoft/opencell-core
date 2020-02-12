package com.opencellsoft.testng.tests.marketingmanager;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.marketingmanager.NewSellersPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author MIFTAH HASSNAA
 *
 */
public class TestNewSellers extends TestBase {
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
        NewSellersPage sellersPage = PageFactory.initElements(this.getDriver(), NewSellersPage.class);
        Thread.sleep(2000);
        /**
         * Go to Sellers page
         */
        sellersPage.gotoListPage(driver);
        /**
         * Go to New Seller Creation page
         */
        sellersPage.gotoNewPage(driver);
        /**
         * Fill the new Sellers form seller 
         */
        sellersPage.fillForm(driver, data);
        /**
         * fill the new Sellers  information
         */
        sellersPage.fillFormInfo(driver, data);
        /**
         * Save the new Sellers
         */
        sellersPage.gotoSave(driver);
    }
}
