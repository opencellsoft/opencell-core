package com.opencellsoft.testng.tests.marketingmanager;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.marketingmanager.MarketingManagerPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestMarketingManager extends TestBase {
    @Test
    public void createMarketingManager() throws InterruptedException {
        
        /**
         * init test
         */
        MarketingManagerPage marketingManagerPage = PageFactory.initElements(this.getDriver(),
            MarketingManagerPage.class);
        
        /**
         * Go to Channels page.
         */
        marketingManagerPage.gotoOffersPage(driver);
        Thread.sleep(8000);
        /**
         * Go to New Channel Creation page.
         */
        marketingManagerPage.checkOffers(driver);
        Thread.sleep(8000);
        marketingManagerPage.gotoProductsPage(driver);
        Thread.sleep(8000);
        marketingManagerPage.checkProducts(driver);
    }

}
