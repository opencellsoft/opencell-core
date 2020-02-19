package com.opencellsoft.testng.tests.setup;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.setup.AssuredFactorPage;
import com.opencellsoft.testng.pages.setup.BrandsPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author Maria AIT BRAHIM.
 *
 */
public class TestBrands extends TestBase {
    
    /**
     * fill the constants.
     */
    public TestBrands() {
        String test = "BR_" + System.currentTimeMillis();

        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        
    }
    
    @Test
    public void testBrandsPage() throws InterruptedException {
        
        
        /**
         * init test.
         */
        BrandsPage brandsPage = PageFactory.initElements(this.getDriver(), BrandsPage.class);
        /**
         * Go to Brands Page.
         */
        brandsPage.gotoListPage(driver);
        brandsPage.goNewPage(driver);
        brandsPage.fillFormCreate(driver, data);
        testData(brandsPage);
        brandsPage.saveOperation(driver);
        brandsPage.searchandelete(driver,data);
        
    }
    /**
     * Check mandatory fields.
     * 
     * @param page instance of  customer  brand
     */
    private void testData(BrandsPage page) {
        String code = page.getCode().getAttribute(ATTRIBUTE_VALUE);
        String description = page.getDescription().getAttribute(ATTRIBUTE_VALUE);
      

        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));
       
    }

}
