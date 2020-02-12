package com.opencellsoft.testng.tests.catalog;

import com.opencellsoft.testng.tests.base.TestBase;
import com.opencellsoft.testng.pages.catalog.OfferCategoriesPage;
import com.opencellsoft.testng.pages.Constants;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
public class TestOfferCategories extends TestBase {
	/**
	 * generate values.
	 */
	public TestOfferCategories() {
		dataKey = String.valueOf(System.currentTimeMillis());
		String str = "OF_" + dataKey;

		data.put(Constants.CODE, str);

	}

	@Test
	public void createOffersCategories() throws InterruptedException {
		OfferCategoriesPage offersCatPage = PageFactory.initElements(this.getDriver(), OfferCategoriesPage.class);
		offersCatPage.gotoListPage(driver);
		offersCatPage.fillData(driver, data);
	}
}
