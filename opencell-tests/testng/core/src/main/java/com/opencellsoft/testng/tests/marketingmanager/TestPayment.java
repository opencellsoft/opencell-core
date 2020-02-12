package com.opencellsoft.testng.tests.marketingmanager;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.marketingmanager.PaymentPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author HASSNAA MIFTAH
 *
 */
public class TestPayment extends TestBase {
    /**
     * test Payment Page.
     * 
     * @throws InterruptedException Exception
     */
    @Test
    public void testPaymentPage() throws InterruptedException {
        /**
         * init 
         */
        PaymentPage paymentPage = PageFactory.initElements(this.getDriver(), PaymentPage.class);
        Thread.sleep(2000);
        paymentPage.gotoPaymentPage(driver);
        Thread.sleep(8000);
        paymentPage.paymentGateWay(driver);
        Thread.sleep(8000);
        
        paymentPage.gotoPaymentPage(driver);
        Thread.sleep(8000);
        
        paymentPage.ddRequestState(driver);
        Thread.sleep(8000);
        
        paymentPage.paymentGateWay(driver);
        Thread.sleep(8000);
        
        paymentPage.ddRequestLot(driver);
    }
}