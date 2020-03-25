package com.opencellsoft.testng.tests.catalog;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.catalog.AllPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Maria AIT BRAHIM
 * 
 */

public class TestAll extends TestBase {
    public static int compteur = 0;
    
    /**
     * All page test.
     * 
     * @throws InterruptedException Exception
     */
    @Test
    private void testAllPage() throws InterruptedException {
        AllPage allPage = PageFactory.initElements(this.getDriver(), AllPage.class);
        allPage.gotoListPage(driver);
        allPage.addTaxe(driver);
    }
}
