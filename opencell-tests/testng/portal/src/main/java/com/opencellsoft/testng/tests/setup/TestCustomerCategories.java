package com.opencellsoft.testng.tests.setup;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.setup.CustomerCategoriesPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestCustomerCategories extends TestBase {
	/**
	 * fill the constants.
	 */
	public TestCustomerCategories() {
		String test = "TCC" + System.currentTimeMillis();
		data.put(Constants.CODE, test);
	}

	@Test
	private void testCreditCategories() throws InterruptedException {
		CustomerCategoriesPage customerCategoriesPage = PageFactory.initElements(this.getDriver(),CustomerCategoriesPage.class);
		customerCategoriesPage.gotoListPage(driver);
		customerCategoriesPage.fillFormCustCat(driver, data);
		testData(customerCategoriesPage);
		customerCategoriesPage.saveCustCat(driver);
		customerCategoriesPage.deleteCustCat(driver, data);

	}

	private void testData(CustomerCategoriesPage customerCategoriesPage) {
		String code = customerCategoriesPage.getCodeCustCat().getAttribute(ATTRIBUTE_VALUE);
		String description = customerCategoriesPage.getDescCustCat().getAttribute(ATTRIBUTE_VALUE);
		
		assertEquals(code, data.get(Constants.CODE));
		assertEquals(description, data.get(Constants.CODE));
	}

}
