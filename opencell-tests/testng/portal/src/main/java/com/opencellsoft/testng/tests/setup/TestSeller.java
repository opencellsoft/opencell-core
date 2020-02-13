package com.opencellsoft.testng.tests.setup;

import static org.testng.Assert.assertEquals;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.setup.SellerPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestSeller extends TestBase {
	/**
	 * fill the constants.
	 */
	public TestSeller() {
		String test = "SE" + System.currentTimeMillis();
		data.put(Constants.CODE, test);
	}

	@Test
	private void testSeller() throws InterruptedException {
		SellerPage sellerPage = PageFactory.initElements(this.getDriver(), SellerPage.class);
		sellerPage.gotoListPage(driver);
		sellerPage.fillFormSeller(driver, data);
		testData(sellerPage);
		sellerPage.saveSeller(driver);
		sellerPage.searchAndDeleteSeller(driver, data);
	}

	private void testData(SellerPage sellerPage) {
		String code = sellerPage.getSellerCode().getAttribute(ATTRIBUTE_VALUE);

		assertEquals(code, data.get(Constants.CODE));
	}

}
