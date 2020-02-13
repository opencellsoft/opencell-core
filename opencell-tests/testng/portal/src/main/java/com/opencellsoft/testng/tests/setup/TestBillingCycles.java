package com.opencellsoft.testng.tests.setup;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.setup.BillingCycles;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * @author  Maria AIT BRAHIM
 *
 */
public class TestBillingCycles extends TestBase {
    /**
     * BillingCyclesTest.
     */
    public TestBillingCycles() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "BC_" + dataKey;

        data.put(Constants.CODE, str);
        data.put(Constants.DESCRIPTION, str);
        data.put(Constants.CODE, str);
        data.put(Constants.DESCRIPTION, str);

    }

    /**
     * create Billing cycle test.
     * @throws InterruptedException 
     */
    @Test
    public void testBillingCyclesPage() throws InterruptedException {
        // Page initialisation
        BillingCycles billingcycles = PageFactory.initElements(this.getDriver(),
            BillingCycles.class);

        // Open billingCycles Page
        billingcycles.gotoListPage(driver);

        // Entering new contact
        billingcycles.goTobtnNew(driver);

        // Entering data
        billingcycles.fillData(driver, data);

        // Saving data
        billingcycles.goToSave(driver);

        // Searching and deleting data
        billingcycles.fillAndSearche(driver, data);
      
    }

}
