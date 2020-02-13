package com.opencellsoft.testng.tests.customers;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.customers.SubscriptionPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestSubscription  extends  TestBase{
    /**
     * generate values.
     */
    public TestSubscription() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "SUB_" + dataKey;
        
        data.put(Constants.CODE, str);
    }
    @Test
    public void createSubscription() throws InterruptedException {
        
        /**
         * init test
         */
        SubscriptionPage subscriptionPage = PageFactory.initElements(this.getDriver(),
            SubscriptionPage.class);
        
        /**
         * Go to CA page.
         */
        subscriptionPage.gotoListPage(driver);
        
        /**
         * Fill CustomerAccount Template.
         */
        subscriptionPage.fillFormSubscription(driver, data);
        
    }
    
    
}
