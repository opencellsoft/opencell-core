package com.opencellsoft.testng.tests.setup;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;

import com.opencellsoft.testng.pages.setup.DiscountPlanPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author Maria AIT BRAHIM.
 *
 */
public class TestDiscountPlan extends TestBase {
    
    /**
     * fill the constants.
     */
    public TestDiscountPlan() {
        String test = "DP_" + System.currentTimeMillis();

        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        
    }
    
    @Test
    public void testDiscountPlanPage() throws InterruptedException {
        
        
        /**
         * init test.
         */
        DiscountPlanPage discountPlanPage = PageFactory.initElements(this.getDriver(), DiscountPlanPage.class);
        
        discountPlanPage.gotoListPage(driver);
        discountPlanPage.goNewPage(driver);
        discountPlanPage.fillFormCreate(driver, data);
        testData(discountPlanPage);
        discountPlanPage.saveOperation(driver);
        discountPlanPage.searchandelete(driver,data);
        
    }
    /**
     * Check mandatory fields.
     * 
     * @param page instance of  discount plan  
     */
    private void testData(DiscountPlanPage page) {
        String code = page.getCode().getAttribute(ATTRIBUTE_VALUE);
        String description = page.getDescription().getAttribute(ATTRIBUTE_VALUE);
      

        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));
       
    }

}
