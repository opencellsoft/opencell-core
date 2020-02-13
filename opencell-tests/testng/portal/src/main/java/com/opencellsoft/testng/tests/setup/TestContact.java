package com.opencellsoft.testng.tests.setup;

import static org.testng.Assert.assertEquals;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.setup.ContactPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestContact extends TestBase {
	/**
	 * fill the constants.
	 */
	public TestContact() {
		String test = "Con" + System.currentTimeMillis();
		data.put(Constants.CODE, test);
	}

	@Test
	private void testContact() throws InterruptedException {
		ContactPage contactPage = PageFactory.initElements(this.getDriver(), ContactPage.class);
		contactPage.gotoListPage(driver);
		contactPage.fillFormContact(driver, data);
		testData(contactPage);
		contactPage.saveContact(driver);
		contactPage.deleteContact(driver, data);

	}

	private void testData(ContactPage contactPage) {
		String code = contactPage.getCodeContact().getAttribute(ATTRIBUTE_VALUE);
		String description = contactPage.getDescContact().getAttribute(ATTRIBUTE_VALUE);
		String email = contactPage.getEmailContact().getAttribute(ATTRIBUTE_VALUE);
		
		assertEquals(code, data.get(Constants.CODE));
		assertEquals(description, data.get(Constants.CODE));
		assertEquals(email, "test@gmail.com");
	}

}
