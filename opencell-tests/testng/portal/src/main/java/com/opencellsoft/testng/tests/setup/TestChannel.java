package com.opencellsoft.testng.tests.setup;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.setup.ChannelsPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestChannel extends TestBase {
	/**
	 * fill the constants.
	 */
	public TestChannel() {
		String test = "CH" + System.currentTimeMillis();
		data.put(Constants.CODE, test);
	}

	@Test
	private void testChannel() throws InterruptedException {
		ChannelsPage channelsPage = PageFactory.initElements(this.getDriver(), ChannelsPage.class);
		channelsPage.gotoListPage(driver);
		channelsPage.fillFormChannel(driver, data);
		testData(channelsPage);
		channelsPage.saveChannel(driver);
		channelsPage.searchChannel(driver, data);
		channelsPage.deleteChannel(driver, data);

	}

	private void testData(ChannelsPage channelsPage) {
		String code = channelsPage.getCodeChannel().getAttribute(ATTRIBUTE_VALUE);
		String description = channelsPage.getDescChannel().getAttribute(ATTRIBUTE_VALUE);
		assertEquals(code, data.get(Constants.CODE));
		assertEquals(description, data.get(Constants.CODE));
	}

}
