package com.opencellsoft.testng.tests.catalog;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.Counters;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * counters page.
 * 
 * @author HP Fatine BELHADJ
 *
 */
public class TestCounters extends TestBase {
    public static int compteur = 0;
    
    /**
     * generate values.
     */
    public TestCounters() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "TC_" + dataKey;
        
        data.put(Constants.CODE, str);
        data.put(Constants.DESCRIPTION, str);
        
    }
    
    /**
     * counters test.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testCountersPage() throws InterruptedException {
        // Page initialisation
        
        Counters counters = PageFactory.initElements(this.getDriver(), Counters.class);
        
        // Open counters model Page
        counters.gotoListPage(driver);
        // Entering data
        counters.fillData(driver, data);
    }
}
