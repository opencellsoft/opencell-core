package com.opencellsoft.testng.tests.catalog;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.OneShotPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Maria AIT BRAHIM
 */
public class TestOneShot extends TestBase {

  /**
   * fill the constants.
   */
  public TestOneShot() {
    String test = "OS_" + System.currentTimeMillis();
    data.put(Constants.CODE, test);
    data.put(Constants.DESCRIPTION, test);

  }

  /**
   * One shot Test.
   * 
   * @throws InterruptedException Exception
   */

  @Test
  private void testOneShotPage() throws InterruptedException {
    OneShotPage oneShotPage =  PageFactory.initElements(this.getDriver(), OneShotPage.class);
    oneShotPage.gotoListPage(driver);
    oneShotPage.fillFormCreate(driver, data);
  }

  /**
   * Check the mandatory fields.
   * 
   * @param page instance of One shot .
   */
  private void testData(OneShotPage page) {
    String code = page.getCode().getAttribute(ATTRIBUTE_VALUE);
    String description = page.getDescription().getAttribute(ATTRIBUTE_VALUE);

    assertEquals(code, data.get(Constants.CODE));
    assertEquals(description, data.get(Constants.DESCRIPTION));

  }

}
