package com.opencellsoft.testng.tests.setup;

import static org.testng.Assert.assertEquals;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.setup.CalendarsPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestCalendars extends TestBase {

	/**
	 * fill the constants.
	 */
	public TestCalendars() {
		String test = "CAL" + System.currentTimeMillis();
		data.put(Constants.CODE, test);
	}

	@Test
	private void testCalendars() throws InterruptedException {
		CalendarsPage calendarsPage = PageFactory.initElements(this.getDriver(), CalendarsPage.class);
		calendarsPage.gotoListPage(driver);
		calendarsPage.fillFormCalendar(driver, data);
		testData(calendarsPage);
		calendarsPage.saveCalendar(driver);
		calendarsPage.searchCalendar(driver, data);
		calendarsPage.deleteCalendar(driver, data);

	}

	private void testData(CalendarsPage calendarsPage) {
		String code = calendarsPage.getCalendarCode().getAttribute(ATTRIBUTE_VALUE);
		String description = calendarsPage.getCalendarDesc().getAttribute(ATTRIBUTE_VALUE);
		assertEquals(code, data.get(Constants.CODE));
		assertEquals(description, data.get(Constants.CODE));
	}

	private void testData2(CalendarsPage calendarsPage) {
		String code = calendarsPage.getCalendarCode().getAttribute(ATTRIBUTE_VALUE);
		assertEquals(code, data.get(Constants.CODE));

	}
}