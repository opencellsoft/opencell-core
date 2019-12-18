package com.opencellsoft.testng.tests.configuration;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.configuration.BrandsPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author MIFTAH HASSNAA
 *
 */
public class TestBrands extends TestBase {
    
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
        
    }
}
