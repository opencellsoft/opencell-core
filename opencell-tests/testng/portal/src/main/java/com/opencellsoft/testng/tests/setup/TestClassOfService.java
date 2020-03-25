package com.opencellsoft.testng.tests.setup;

import static org.testng.Assert.assertEquals;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.setup.ClassOfServicePage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestClassOfService extends TestBase {
	/**
	 * fill the constants.
	 */
	public TestClassOfService() {
		String test = "ClassSer" + System.currentTimeMillis();
		data.put(Constants.CODE, test);
	}

	@Test
	private void testClassOfService() throws InterruptedException {
		ClassOfServicePage classOfServicePage = PageFactory.initElements(this.getDriver(), ClassOfServicePage.class);
		classOfServicePage.gotoListPage(driver);
		classOfServicePage.fillFormClassService(driver, data);
		testData(classOfServicePage);
		classOfServicePage.saveClassService(driver);
		classOfServicePage.deleteClassService(driver, data);

	}

	private void testData(ClassOfServicePage classOfServicePage) {
		String code = classOfServicePage.getCodeClassService().getAttribute(ATTRIBUTE_VALUE);
		String description = classOfServicePage.getDescClassService().getAttribute(ATTRIBUTE_VALUE);
		assertEquals(code, data.get(Constants.CODE));
		assertEquals(description, data.get(Constants.CODE));
	}

}
