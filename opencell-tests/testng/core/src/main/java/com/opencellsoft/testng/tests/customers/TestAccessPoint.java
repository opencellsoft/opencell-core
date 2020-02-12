package com.opencellsoft.testng.tests.customers;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.customers.AccessPointPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestAccessPoint extends  TestBase {
    /**
     * generate values.
     */
    public TestAccessPoint() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "AP_" + dataKey;
        
        data.put(Constants.CODE, str);
    }
    @Test
    public void createAccessPoint() throws InterruptedException {
        
        /**
         * init test
         */
        AccessPointPage accessPointPage = PageFactory.initElements(this.getDriver(),
            AccessPointPage.class);
        
        /**
         * Go to Access Point page.
         */
        accessPointPage.gotoListPage(driver);
        
        /**
         * Fill FormAccessPoint Template.
         */
        accessPointPage.fillFormAccessPoint(driver, data);
        
    }
    
    
    
}
