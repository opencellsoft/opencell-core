package com.opencellsoft.testng.tests.customers;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.customers.CustomerPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestCustomer extends TestBase {
	  /**
     * fill the constants.
     */
    public TestCustomer() {
        String test = "CUST_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);        
    }
	  @Test
	    private void testCustomersPage() throws InterruptedException {
	        CustomerPage customerPage = PageFactory.initElements(this.getDriver(), CustomerPage.class);
	        customerPage.gotoListPage(driver);
	        customerPage.fillFormCustomer(driver,data);
	    }
	    
}
