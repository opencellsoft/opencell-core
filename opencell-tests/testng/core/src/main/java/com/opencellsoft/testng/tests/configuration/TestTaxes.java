package com.opencellsoft.testng.tests.configuration;

import static org.testng.Assert.assertEquals;

import com.opencellsoft.testng.pages.Constants;

import com.opencellsoft.testng.pages.configuration.TaxesPage;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * 
 * @author Maria AIT BRAHIM
 * 
 */

public class TestTaxes extends TestBase {

    /**
     * fill the constants.
     */
    public TestTaxes() {
        String test = "RE_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);

    }

    /**
     * Taxes test.
     * @throws InterruptedException 
     */
    @Test
    private void testTaxesPage() throws InterruptedException {
        TaxesPage taxesPage = PageFactory.initElements(this.getDriver(), TaxesPage.class);
        taxesPage.gotoListPage(driver);
        taxesPage.gotoNewPage(driver);
        taxesPage.fillFormCreate(driver, data);
        //testData(taxesPage);
        taxesPage.save(driver);
        taxesPage.fillFormAndSearch(driver, data);
        taxesPage.delete(driver);
    }

    /**
     * Check the mandatory fields.
     * 
     * @param page instance of TaxesPage
     */
    private void testData(TaxesPage page) {

        String code = page.getCode().getAttribute(ATTRIBUTE_VALUE);
        String description = page.getDescription().getAttribute(ATTRIBUTE_VALUE);

        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));

    }

}
