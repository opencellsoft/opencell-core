package com.opencellsoft.testng.tests.offers;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.offers.Offers;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * offer model.
 * 
 * @author AIT BRAHIM Maria
 *
 */
public class TestOffer extends TestBase {
  /**
   * generate values.
   */
  public TestOffer() {
    dataKey = String.valueOf(System.currentTimeMillis());
    String str = "OM_" + dataKey;

    data.put(Constants.CODE, str);
    data.put(Constants.DESCRIPTION, str);

  }

  /**
   * Offer Model.
 * @throws InterruptedException 
   */
  @Test
  public void testOfferPage() throws InterruptedException {
    // Page initialisation

      Offers offerModel = PageFactory.initElements(this.getDriver(), Offers.class);

    // Open offer model Page
    offerModel.gotoListPage(driver);
    // Entering data
    offerModel.fillData(driver, data);
   
  }

}
