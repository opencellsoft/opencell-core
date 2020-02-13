package com.opencellsoft.testng.tests.reporting;

import static org.testng.Assert.assertEquals;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.reporting.MQPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Miftah
 *
 */
public class TestMQ extends TestBase {
    /**
     * generate values.
     */
    public TestMQ() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "MQ_" + dataKey;
        
        data.put(Constants.CODE, str);
    }
    
    /**
     * ReportExtract Model.
     */
    @Test
    /**
     * test TestReportExtract page .
     * 
     * @throws InterruptedException Exception
     */
    public void testMQ() throws InterruptedException {
        
        MQPage mesurableQuantities = PageFactory.initElements(this.getDriver(), MQPage.class);
        mesurableQuantities.openMQList(driver);
        mesurableQuantities.fillMQ(driver, data);
        mesurableQuantities.saveMQ(driver);
        mesurableQuantities.searchMQAndDelete(driver, data);
    }


}
