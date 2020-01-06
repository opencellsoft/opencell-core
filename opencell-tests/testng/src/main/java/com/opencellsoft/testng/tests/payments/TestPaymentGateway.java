package com.opencellsoft.testng.tests.payments;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.payments.PaymentGatewayPage;
import com.opencellsoft.testng.tests.base.TestBase;
/**
 * 
 * @author Miftah
 *
 */
public class TestPaymentGateway extends TestBase {
    /**
     * generate values.
     */
    public TestPaymentGateway() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "PG_" + dataKey;
        
        data.put(Constants.CODE, str);
    }
    @Test
    public void testPaymentGateway() throws InterruptedException { {    
        PaymentGatewayPage paymentGatewayPage = PageFactory.initElements(this.getDriver(),
            PaymentGatewayPage.class);
        paymentGatewayPage.openPSPPage(driver);
        paymentGatewayPage.fillPaymentGatewayAndSave(driver, data);
    
}
    }}
