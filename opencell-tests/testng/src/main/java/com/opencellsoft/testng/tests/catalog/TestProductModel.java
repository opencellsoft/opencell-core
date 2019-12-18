package com.opencellsoft.testng.tests.catalog;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.ProductModels;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * Product model test.
 * 
 * @author HP Fatine BELHADJ
 *
 */
public class TestProductModel extends TestBase {
    /**
     * generate values.
     */
    public TestProductModel() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "TC_" + dataKey;
        
        data.put(Constants.CODE, str);
        data.put(Constants.DESCRIPTION, str);
        
    }
    
    /**
     * Offer Model.
     * 
     * @throws InterruptedException
     */
    @Test
    public void create() throws InterruptedException {
        // Page initialisation
        
        ProductModels productModels = PageFactory.initElements(this.getDriver(),
            ProductModels.class);
        
        // Open offer model Page
        productModels.gotoListPage(driver);
        
        // Entering data
        productModels.fillData(driver, data);
    }
    
}
