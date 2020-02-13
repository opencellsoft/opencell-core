package com.opencellsoft.testng.tests.payments;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.payments.PaymentGatewaysPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestPaymentGateways extends TestBase {
	/**
	 * fill the constants.
	 */
	public TestPaymentGateways() {
		String test = "GAT" + System.currentTimeMillis();
		data.put(Constants.CODE, test);
	}
	@Test
	private void testPaymentGateways() throws InterruptedException {
		PaymentGatewaysPage paymentGatewaysPage = PageFactory.initElements(this.getDriver(), PaymentGatewaysPage.class);
		paymentGatewaysPage.gotoListPage(driver);
		paymentGatewaysPage.fillFormGateway(driver, data);
		testData(paymentGatewaysPage);
		paymentGatewaysPage.saveGateway(driver);
		paymentGatewaysPage.searchAndDeleteGateway(driver, data);
	}

	private void testData(PaymentGatewaysPage paymentGatewaysPage) {
		String code = paymentGatewaysPage.getGatewayCode().getAttribute(ATTRIBUTE_VALUE);

		assertEquals(code, data.get(Constants.CODE));
	}

}
