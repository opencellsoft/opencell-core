package com.opencellsoft.testng.tests.catalog;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.CatalogRecurringPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author HASSNAA MIFTAH
 *
 */
public class TestCatalogRecurring extends TestBase {
    /**
     * fill the constants.
     */
    public TestCatalogRecurring() {
      String test = "REC_" + System.currentTimeMillis();
      data.put(Constants.CODE, test);
    }
    /**
     * test Catalog Recurring Page method .
     * @throws InterruptedException 
     */
    @Test
    private void testCatalogRecurringPage() throws InterruptedException {
        CatalogRecurringPage detailPage = PageFactory.initElements(this.getDriver(),
            CatalogRecurringPage.class);
        detailPage.gotoListPage(driver);
        detailPage.fillFormAndSave(driver, data);
    }
}
