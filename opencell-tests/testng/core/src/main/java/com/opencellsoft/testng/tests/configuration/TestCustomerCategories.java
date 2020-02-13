package com.opencellsoft.testng.tests.configuration;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.configuration.CustomerCategoriesPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Hassnaa MIFTAH
 *
 */
public class TestCustomerCategories extends TestBase {
    /**
     * generate random values
     */
    public TestCustomerCategories() {
        String test = "RE_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        data.put(Constants.NTAEL, test);
        data.put(Constants.NTR, test);
    }

    /**
     * create test of Customer categories.
     * @throws InterruptedException 
     */
    @Test
    public void createCustomercat() throws InterruptedException {
        /**
         * init test
         */
        CustomerCategoriesPage customerCategoriesPage = PageFactory.initElements(this.getDriver(),
            CustomerCategoriesPage.class);
        /**
         * Go to Customer category page
         */
        customerCategoriesPage.gotoListPage(driver);
        /**
         * Go to New Customer category page
         */
        customerCategoriesPage.gotoNewPage(driver);
        /**
         * Fill the new Customer category form
         */
        customerCategoriesPage.fillForm(driver, data);
        /**
         * Save the new Customer category
         */
        customerCategoriesPage.gotoSave(driver);
        /**
         * Search for the new created Customer category and delete it
         */
        customerCategoriesPage.searchandDelete(driver, data);
    }
}
