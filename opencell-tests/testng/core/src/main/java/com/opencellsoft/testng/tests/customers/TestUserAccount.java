package com.opencellsoft.testng.tests.customers;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.customers.UserAccountPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestUserAccount extends  TestBase {
    /**
     * generate values.
     */
    public TestUserAccount() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "UA_" + dataKey;
        
        data.put(Constants.CODE, str);
    }
    @Test
    public void createUA() throws InterruptedException {
        
        /**
         * init test
         */
        UserAccountPage userAccountPage = PageFactory.initElements(this.getDriver(),
            UserAccountPage.class);
        
        /**
         * Go to CA page.
         */
        userAccountPage.gotoListPage(driver);
        
        /**
         * Fill CustomerAccount Template.
         */
        userAccountPage.fillFormUserAccount(driver, data);
        
    }
    
}
