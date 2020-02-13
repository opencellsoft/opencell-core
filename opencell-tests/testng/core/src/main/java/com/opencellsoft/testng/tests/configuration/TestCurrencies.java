package com.opencellsoft.testng.tests.configuration;

import com.opencellsoft.testng.pages.configuration.Currencies;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * @author Fatine BELHADJ
 *
 */
public class TestCurrencies extends TestBase {
    /**
     * Create TestCurrencies.
     * 
     * @throws InterruptedException
     */
    @Test
    public void create() throws InterruptedException {
        // Page initialisation.
        Currencies currencies = PageFactory.initElements(this.getDriver(), Currencies.class);
        
        // Open Currency Page
        currencies.gotoListPage(driver);
        
        // Choose a currency
        currencies.fillCurrencies(driver);
        
    }
    
}
