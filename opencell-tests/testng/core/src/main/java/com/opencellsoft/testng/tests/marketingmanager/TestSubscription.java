package com.opencellsoft.testng.tests.marketingmanager;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.marketingmanager.SubscriptionPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author HASSNAA MIFTAH
 *
 */
public class TestSubscription extends TestBase {
    
    /**
     * Test sellers page .
     * 
     * @throws InterruptedException Exception .
     */
    @Test
    public void testSellerPage() throws InterruptedException {
        /**
         * init test .
         */
        SubscriptionPage subscriptionPage = PageFactory.initElements(this.getDriver(),
            SubscriptionPage.class);
        Thread.sleep(2000);
        subscriptionPage.gotoSubscriptionPage(driver);
        Thread.sleep(8000);
        
        subscriptionPage.showSubscription(driver);
        
    }
}