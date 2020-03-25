package com.opencellsoft.testng.tests.finance;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.finance.AccountingCodePage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestAccountingCode extends TestBase {
	/**
	 * fill the constants.
	 */
	public TestAccountingCode() {
		String test = "ACC" + System.currentTimeMillis();
		data.put(Constants.CODE, test);
	}

	@Test
	private void testAccountingCode() throws InterruptedException {
		AccountingCodePage accountingCodePage = PageFactory.initElements(this.getDriver(), AccountingCodePage.class);
		accountingCodePage.gotoListPage(driver);
		accountingCodePage.fillFormAccountCode(driver, data);
		testData(accountingCodePage);
		accountingCodePage.saveAccountingCode(driver);
		accountingCodePage.searchAndDeleteAccountCode(driver, data);
	}

	private void testData(AccountingCodePage accountingCodePage) {
		String code = accountingCodePage.getAccountingCode().getAttribute(ATTRIBUTE_VALUE);

		assertEquals(code, data.get(Constants.CODE));
	}
}
