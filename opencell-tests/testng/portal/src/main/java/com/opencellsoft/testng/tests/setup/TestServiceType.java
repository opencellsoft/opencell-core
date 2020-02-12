package com.opencellsoft.testng.tests.setup;

import static org.testng.Assert.assertEquals;

import java.util.Random;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.setup.ServiceTypePage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestServiceType extends TestBase {
	/**
	 * fill the constants.
	 */
	public TestServiceType() {
		// initialize a Random object somewhere; you should only need one
		Random random = new Random();
		// generate a random integer from 0 to 899, then add 100
		int x = random.nextInt(99);
		String test = "ST" + x;
		data.put(Constants.CODE, test);
	}

	@Test
	private void testServiceType() throws InterruptedException {
		ServiceTypePage serviceTypePage = PageFactory.initElements(this.getDriver(), ServiceTypePage.class);
		serviceTypePage.gotoListPage(driver);
		serviceTypePage.fillFormServiceType(driver, data);
		testData(serviceTypePage);
		serviceTypePage.saveInvSeq(driver);
		serviceTypePage.searchInvSeq(driver, data);
		serviceTypePage.deleteInvSeq(driver, data);
	}

	private void testData(ServiceTypePage serviceTypePage) {
		String code = serviceTypePage.getCodeServiceType().getAttribute(ATTRIBUTE_VALUE);
		assertEquals(code, data.get(Constants.CODE));
	}

}
