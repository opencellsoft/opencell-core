package com.opencellsoft.testng.tests.catalog;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.ProductChargesPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author MIFTAH
 */
public class TestProductCharges extends TestBase {
    
    /**
     * fill the constants.
     */
    public TestProductCharges() {
        String test = "PC_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        
    }
    
    /**
     * Product Charges Test.
     * 
     * @throws InterruptedException Exception
     */
    
    @Test
    private void testProductChargesPage() throws InterruptedException {
        ProductChargesPage productChargesPage = PageFactory.initElements(this.getDriver(),
            ProductChargesPage.class);
        productChargesPage.gotoListPage(driver);
        productChargesPage.fillFormCreate(driver, data);
        
    }
    
}
