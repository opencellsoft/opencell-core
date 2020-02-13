package com.opencellsoft.testng.tests.setup;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.setup.ActionQualifierPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestActionQualifier extends TestBase {
	/**
	 * fill the constants.
	 */
	public TestActionQualifier() {
		String test = "AQ" + System.currentTimeMillis();
		data.put(Constants.CODE, test);
	}
	@Test
	private void testActionQualifier() throws InterruptedException {
		ActionQualifierPage actionQualifierPage = PageFactory.initElements(this.getDriver(), ActionQualifierPage.class);
		actionQualifierPage.gotoListPage(driver);
		actionQualifierPage.fillFormAction(driver, data);
		testData(actionQualifierPage);
		actionQualifierPage.saveAction(driver);
	}

	private void testData(ActionQualifierPage actionQualifierPage) {
		String code = actionQualifierPage.getActionQualifierCode().getAttribute(ATTRIBUTE_VALUE);

		assertEquals(code, data.get(Constants.CODE));
	}


}
