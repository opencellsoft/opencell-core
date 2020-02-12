package com.opencellsoft.testng.tests.catalog;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.ServicePage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Maria AIT BRAHIM
 */
public class TestService extends TestBase {

  /**
   * fill the constants.
   */
  public TestService() {
    String test = "SE_" + System.currentTimeMillis();
    data.put(Constants.CODE, test);
    data.put(Constants.DESCRIPTION, test);
    data.put(Constants.LONG_DESCRIPTION, test);

  }

  /**
   * Service Test.
   * 
   * @throws InterruptedException Exception
   */

  @Test
  private void testServicePage() throws InterruptedException {
    ServicePage servicePage = PageFactory.initElements(this.getDriver(), ServicePage.class);
    servicePage.gotoListPage(driver);
    servicePage.fillFormCreate(driver, data);
  }

  /**
   * Check the mandatory fields.
   * 
   * @param page instance of Service .
   */
  private void testData(ServicePage page) {
    String code = page.getCode().getAttribute(ATTRIBUTE_VALUE);
    String description = page.getDescription().getAttribute(ATTRIBUTE_VALUE);

    assertEquals(code, data.get(Constants.CODE));
    assertEquals(description, data.get(Constants.DESCRIPTION));

  }

}
