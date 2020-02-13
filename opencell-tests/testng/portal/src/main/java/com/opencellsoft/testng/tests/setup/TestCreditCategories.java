package com.opencellsoft.testng.tests.setup;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.setup.CreditCategoriesPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestCreditCategories extends TestBase {
	/**
	 * fill the constants.
	 */
	public TestCreditCategories() {
		String test = "TCC" + System.currentTimeMillis();
		data.put(Constants.CODE, test);
	}

	@Test
	private void testCreditCategories() throws InterruptedException {
		CreditCategoriesPage creditCategoriesPage = PageFactory.initElements(this.getDriver(),CreditCategoriesPage.class);
		creditCategoriesPage.gotoListPage(driver);
		creditCategoriesPage.fillFormCreditCat(driver, data);
		testData(creditCategoriesPage);
		creditCategoriesPage.saveCreditCat(driver);
		creditCategoriesPage.deleteCreditCat(driver, data);

	}

	private void testData(CreditCategoriesPage creditCategoriesPage) {
		String code = creditCategoriesPage.getCodeCreditCat().getAttribute(ATTRIBUTE_VALUE);
		String description = creditCategoriesPage.getDescCreditCat().getAttribute(ATTRIBUTE_VALUE);
		
		assertEquals(code, data.get(Constants.CODE));
		assertEquals(description, data.get(Constants.CODE));
	}

}
