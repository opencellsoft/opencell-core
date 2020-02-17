package com.opencellsoft.testng.tests.setup;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;

import com.opencellsoft.testng.pages.setup.DensityPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author Maria AIT BRAHIM.
 *
 */
public class TestDensity extends TestBase {
    
    /**
     * fill the constants.
     */
    public TestDensity() {
        String test = "BR_" + System.currentTimeMillis();
        
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        
    }
    
    @Test
    public void testdensityPage() throws InterruptedException {
        
        /**
         * init test.
         */
        DensityPage densityPage = PageFactory.initElements(this.getDriver(), DensityPage.class);
        /**
         * Go to Brands Page.
         */
        densityPage.gotoListPage(driver);
        densityPage.goNewPage(driver);
        densityPage.fillFormCreate(driver, data);
        testData(densityPage);
        densityPage.saveOperation(driver);
        //densityPage.searchandelete(driver, data);
        
    }
    
    /**
     * Check mandatory fields.
     * 
     * @param page instance of customer brand
     */
    private void testData(DensityPage page) {
        String code = page.getCode().getAttribute(ATTRIBUTE_VALUE);
        String description = page.getDescription().getAttribute(ATTRIBUTE_VALUE);
        
        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));
        
    }
    
}
