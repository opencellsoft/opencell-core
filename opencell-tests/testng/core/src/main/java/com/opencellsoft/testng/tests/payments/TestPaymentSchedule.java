package com.opencellsoft.testng.tests.payments;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.payments.PaymentSchedulePage;
import com.opencellsoft.testng.tests.base.TestBase;
/**
 * 
 * @author Miftah
 *
 */
public class TestPaymentSchedule extends TestBase {
    /**
     * generate values.
     */
    public TestPaymentSchedule() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "PS_" + dataKey;
        
        data.put(Constants.CODE, str);
    }
        @Test
        public void testPaymentSchedule() throws InterruptedException { {   
            PaymentSchedulePage paymentSchedulePage = PageFactory.initElements(this.getDriver(),
                PaymentSchedulePage.class);
            paymentSchedulePage.openPSPPage(driver);
            paymentSchedulePage.fillPaymentScheduleAndSave(driver, data);  
            paymentSchedulePage.searchPaymentScheduleAndDelete(driver, data); 
        }
        }
}
