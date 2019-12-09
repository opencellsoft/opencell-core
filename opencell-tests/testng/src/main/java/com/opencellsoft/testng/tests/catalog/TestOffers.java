package com.opencellsoft.testng.tests.catalog;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.OffersPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author HASSNAA MIFTAH 
 *
 */
public class TestOffers extends TestBase {
    
    /**
     * generate values.
     */
    public TestOffers() {
      dataKey = String.valueOf(System.currentTimeMillis());
      String str = "OF_" + dataKey;

      data.put(Constants.CODE, str);
      data.put(Constants.DESCRIPTION, str);

    }
    
    /**
     * create a test for offers Page .
     * 
     * @throws InterruptedException Exception
     */
    
    @Test
    public void createOffers() throws InterruptedException {
        
        /**
         * init test
         */
        OffersPage offersPage = PageFactory.initElements(this.getDriver(), OffersPage.class);
        
        /**
         * Go to Offers page.
         */    
        offersPage.gotoListPage(driver);

        /**
         * Fill the New Offer Template.
         */
        offersPage.fillInformationsForm(driver, data);  
    }
    
}
