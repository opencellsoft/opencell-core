package com.opencellsoft.testng.tests.configuration;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.configuration.CreditCategoriesPage;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * @author Fatine BELHADJ
 *
 */

public class TestCreditCategories extends TestBase {
    /**
     * Default constructor.
     */
    public TestCreditCategories() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "TC_" + dataKey;
        data.put(Constants.CODE, str);
        data.put(Constants.DESCRIPTION, str);
    }

    /**
     * create credit categories test.
     * @throws InterruptedException 
     */
    @Test
    public void create() throws InterruptedException {
        /**
         * Page initialisation.
         */
        CreditCategoriesPage creditcategoriespage = PageFactory.initElements(this.getDriver(),
            CreditCategoriesPage.class);

        // Open CreditCategories Page
        creditcategoriespage.gotoListPage(driver);
        // Fill fields with new data
        creditcategoriespage.fillCreditCategoriNew(driver, data);


    }

}
