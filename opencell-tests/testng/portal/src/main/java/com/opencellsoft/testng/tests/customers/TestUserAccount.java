package com.opencellsoft.testng.tests.customers;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.customers.UserAccountPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestUserAccount extends TestBase {
	  /**
     * fill the constants.
     */
    public TestUserAccount() {
        String test = "UA_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);        
    } 
    @Test
    private void testUserAccount() throws InterruptedException {
	    UserAccountPage userAccountPage = PageFactory.initElements(this.getDriver(), UserAccountPage.class);
	    userAccountPage.gotoListPage(driver);
	    userAccountPage.fillFormUserAccount(driver,data);
    }
}
