package com.opencellsoft.testng.tests.customers;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.customers.CustomerAccountPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestCustomerAccount extends TestBase {
    
    /**
     * generate values.
     */
    public TestCustomerAccount() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "CA_" + dataKey;
        
        data.put(Constants.CODE, str);
    }
    
    @Test
    public void createCustomers() throws InterruptedException {
        
        /**
         * init test
         */
        CustomerAccountPage customerAccountPage = PageFactory.initElements(this.getDriver(),
            CustomerAccountPage.class);
        
        /**
         * Go to CA page.
         */
        customerAccountPage.gotoListPage(driver);
        
        /**
         * Fill CustomerAccount Template.
         */
        customerAccountPage.fillFormCustomerAccount(driver, data);
        
    }
    
}
