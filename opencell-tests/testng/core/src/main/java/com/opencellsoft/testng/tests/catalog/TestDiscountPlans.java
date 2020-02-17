package com.opencellsoft.testng.tests.catalog;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.DiscountPlansPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Miftah
 */
public class TestDiscountPlans extends TestBase {

  /**
   * fill the constants.
   */
  public TestDiscountPlans() {
    String test = "DP_" + System.currentTimeMillis();
    data.put(Constants.CODE, test);
    data.put(Constants.DESCRIPTION, test);

  }

  /**
   * Discount plan Test.
   * 
   * @throws InterruptedException Exception
   */

  @Test
  private void testDiscountPlanPage() throws InterruptedException {
    DiscountPlansPage discountPlansPage = PageFactory.initElements(this.getDriver(), DiscountPlansPage.class);
    discountPlansPage.gotoListPage(driver);
    discountPlansPage.fillFormCreate(driver, data);
    //testData(discountPlansPage);
    //discountPlansPage.save(driver);

  }

}
