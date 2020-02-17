package com.opencellsoft.testng.tests.configuration;

import static org.testng.Assert.assertEquals;

import com.opencellsoft.testng.pages.Constants;

import com.opencellsoft.testng.pages.configuration.CustomersTitlesCivilitiesPage;
import com.opencellsoft.testng.tests.base.TestBase;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 *
 * @author Maria AIT BRAHIM
 */
public class TestCustomersTitlesCivilities extends TestBase {

    /**
     * fill the constants.
     */

    public TestCustomersTitlesCivilities() {
        String test = "RE_" + System.currentTimeMillis();

        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        data.put(Constants.CIVILITY_ENGLISH, test);
        data.put(Constants.CIVILITY_FRENCH, test);

    }

    /**
     * Customers Titles Civilities test.
     * @throws InterruptedException 
     * 
     */

    @Test
    private void testCustomerTitleCivilities() throws InterruptedException {

        CustomersTitlesCivilitiesPage customersTitlesCivilitiesPage = PageFactory
            .initElements(this.getDriver(), CustomersTitlesCivilitiesPage.class);
        customersTitlesCivilitiesPage.gotoListPage(driver);
        customersTitlesCivilitiesPage.gotoNewPage(driver);
        customersTitlesCivilitiesPage.fillFormCreate(driver, data);
        testData(customersTitlesCivilitiesPage);
        customersTitlesCivilitiesPage.saveTitles(driver);
        customersTitlesCivilitiesPage.fillFormAndSearch(driver, data);
        customersTitlesCivilitiesPage.deleteCalendar(driver);
    }

    /**
     * Check the mandatory fields.
     * 
     * @param page instance of CustomersTitlesCivilities
     */

    private void testData(CustomersTitlesCivilitiesPage page) {

        String code = page.getCodeCtp().getAttribute(ATTRIBUTE_VALUE);
        String description = page.getDescriptionCtp().getAttribute(ATTRIBUTE_VALUE);
        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));

    }

}