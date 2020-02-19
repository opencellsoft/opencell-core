package com.opencellsoft.testng.tests.catalog;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.PricePlansPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Maria AIT BRAHIM
 */
public class TestPricePlans extends TestBase {
    
    /**
     * fill the constants.
     */
    public TestPricePlans() {
        String test = "PP_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        
    }
    
    /**
     * Price plan Test.
     * 
     * @throws InterruptedException Exception
     */
    
    @Test
    private void testPricePlanPage() throws InterruptedException {
        PricePlansPage pricePlansPage = PageFactory.initElements(this.getDriver(),
            PricePlansPage.class);
        pricePlansPage.gotoListPage(driver);
        pricePlansPage.fillFormCreate(driver, data);
    }    
    /**
     * Check the mandatory fields.
     * 
     * @param page instance of Price Plans .
     */
    private void testData(PricePlansPage page) {
        String code = page.getCode().getAttribute(ATTRIBUTE_VALUE);
        String description = page.getDescription().getAttribute(ATTRIBUTE_VALUE);
        
        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));
        
    }
    
}
