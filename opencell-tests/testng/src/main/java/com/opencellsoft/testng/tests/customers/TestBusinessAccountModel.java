package com.opencellsoft.testng.tests.customers;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.customers.BusinessAccountModelsPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestBusinessAccountModel extends TestBase {
    /**
     * generate values.
     */
    public TestBusinessAccountModel() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "AP_" + dataKey;
        
        data.put(Constants.CODE, str);
    }
    
    @Test
    public void createBusinessAccountModel() throws InterruptedException {
        
        /**
         * init test
         */
        BusinessAccountModelsPage businessAccountModelsPage = PageFactory
            .initElements(this.getDriver(), BusinessAccountModelsPage.class);
        
        /**
         * Go to Access Point page.
         */
        businessAccountModelsPage.gotoListPage(driver);
        
        /**
         * Fill FormAccessPoint Template.
         */
        businessAccountModelsPage.fillFormbusinessAccountModels(driver, data);
        
    }
    
}
