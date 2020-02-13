package com.opencellsoft.testng.tests.configuration;

import com.opencellsoft.testng.pages.configuration.Country;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * @author Fatine BELHADJ
 *
 */
public class TestCountries extends TestBase {

    /**
     * create countries test.
     */
    @Test
    public void create() {
        /**
         * Page initialisation.
         */
        Country country = PageFactory.initElements(this.getDriver(), Country.class);

        // Open Trading Language Page
        country.gotoListPage(driver);

        // Entering new trading language
        country.goTobtnNew(driver);

        // Entering new trading language
        country.fillCountry(driver);

        // Reset data
        country.goTobtnReset(driver);

        // Entering country data
        country.fillCountry(driver);

        country.goTobtnSave(driver);

        country.delete(driver);
    }

}
