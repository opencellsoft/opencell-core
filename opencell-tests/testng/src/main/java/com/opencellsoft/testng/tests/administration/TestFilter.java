package com.opencellsoft.testng.tests.administration;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.administration.FilterPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Maria AIT BRAHIM
 */
public class TestFilter extends TestBase  {

  /**
   * fill the constants.
   */
  public TestFilter() {
    String test = "FIL_" + System.currentTimeMillis();
    data.put(Constants.CODE, test);
    data.put(Constants.DESCRIPTION, test);

  }

  /**
   * Filter Test.
   * 
   * @throws InterruptedException Exception
   */

  @Test
  private void testFilterPage() throws InterruptedException {
    FilterPage filterPage = PageFactory.initElements(this.getDriver(), FilterPage.class);
    filterPage.gotoListPage(driver);
    filterPage.fillFormCreate(driver, data);
    //testData(filterPage);
  }

  /**
   * Check the mandatory fields.
   * 
   * @param page instance of Filter .
   */
  private void testData(FilterPage page) {
    String code = page.getCode().getAttribute(ATTRIBUTE_VALUE);
    String description = page.getDescription().getAttribute(ATTRIBUTE_VALUE);

    assertEquals(code, data.get(Constants.CODE));
    assertEquals(description, data.get(Constants.DESCRIPTION));

  }

}
