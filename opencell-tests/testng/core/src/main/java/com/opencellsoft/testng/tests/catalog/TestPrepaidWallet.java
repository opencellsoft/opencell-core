package com.opencellsoft.testng.tests.catalog;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.PrepaidWalletsPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Maria AIT BRAHIM
 * 
 */
public class TestPrepaidWallet extends TestBase {

  /**
   * fill the constants.
   */
  public TestPrepaidWallet() {
    String test = "RE_" + System.currentTimeMillis();
    data.put(Constants.CODE, test);
    data.put(Constants.DESCRIPTION, test);

  }

  /**
   * prepaid wallets test.
   * 
   * @throws InterruptedException Exception
   */

  @Test
  private void testprepaidWallets() throws InterruptedException {
    PrepaidWalletsPage prepaidWalletsPage = PageFactory.initElements(this.getDriver(), PrepaidWalletsPage.class);
    prepaidWalletsPage.gotoListPage(driver);
    prepaidWalletsPage.fillFormCreate(driver, data);

  }

  /**
   * Check the mandatory fields.
   * 
   * @param page instance of prepaid wallets .
   */
  private void testData(PrepaidWalletsPage page) {
    String code = page.getCode().getAttribute(ATTRIBUTE_VALUE);
    String description = page.getDescription().getAttribute(ATTRIBUTE_VALUE);

    assertEquals(code, data.get(Constants.CODE));
    assertEquals(description, data.get(Constants.DESCRIPTION));

  }
}
