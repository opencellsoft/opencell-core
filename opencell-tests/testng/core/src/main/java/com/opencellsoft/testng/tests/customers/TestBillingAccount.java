package com.opencellsoft.testng.tests.customers;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.customers.BillingAccountPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestBillingAccount extends  TestBase {
    /**
     * generate values.
     */
    public TestBillingAccount() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "BA_" + dataKey;
        
        data.put(Constants.CODE, str);
    }
    @Test
    public void createBA() throws InterruptedException {
        
        /**
         * init test
         */
        BillingAccountPage billingAccountPage = PageFactory.initElements(this.getDriver(),
            BillingAccountPage.class);
        
        /**
         * Go to CA page.
         */
        billingAccountPage.gotoListPage(driver);
        
        /**
         * Fill CustomerAccount Template.
         */
        billingAccountPage.fillFormBillingAccount(driver, data);
        
    }
    
}

