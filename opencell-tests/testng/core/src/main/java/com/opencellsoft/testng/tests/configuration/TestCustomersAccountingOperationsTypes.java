package com.opencellsoft.testng.tests.configuration;

import static org.testng.Assert.assertEquals;

import com.opencellsoft.testng.pages.Constants;

import com.opencellsoft.testng.pages.configuration.CustomersAccountingOperationsTypesPage;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * 
 * @author Maria AIT BRAHIM
 */
public class TestCustomersAccountingOperationsTypes extends TestBase {

    /**
     * fill the constants.
     */
    public TestCustomersAccountingOperationsTypes() {
        String test = "OCC_" + System.currentTimeMillis();

        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        data.put(Constants.ACCOUNTCCSIDE, test);

    }

    /**
     * Customers Accounting Operations Types test.
     * @throws InterruptedException 
     */

    @Test
    private void testAccountingOperationsTypes() throws InterruptedException {
        CustomersAccountingOperationsTypesPage customersAccountingOperationsTypesPage = PageFactory
            .initElements(this.getDriver(), CustomersAccountingOperationsTypesPage.class);
        customersAccountingOperationsTypesPage.gotoListPage(driver);
        customersAccountingOperationsTypesPage.gotoNewPage(driver);
        customersAccountingOperationsTypesPage.fillFormCreate(driver, data);
        //testData(customersAccountingOperationsTypesPage);
        customersAccountingOperationsTypesPage.saveOperation(driver);
        customersAccountingOperationsTypesPage.fillFormAndSearch(driver, data);
        customersAccountingOperationsTypesPage.deleteOperation(driver);
    }

    /**
     * Check mandatory fields.
     * 
     * @param page instance of CustomersAccountingOperationsTypes
     */
    private void testData(CustomersAccountingOperationsTypesPage page) {
        String code = page.getCodeCtp().getAttribute(ATTRIBUTE_VALUE);
        String description = page.getDescriptionCtp().getAttribute(ATTRIBUTE_VALUE);
        String accountCode = page.getAccCode().getAttribute(ATTRIBUTE_VALUE);

        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));
        assertEquals(accountCode, data.get(Constants.ACCOUNTCCSIDE));

    }

}
