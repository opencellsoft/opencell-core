package com.opencellsoft.testng.tests.catalog;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.OfferModels;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * offer model.
 * 
 * @author Miftah
 *
 */
public class TestOfferModel extends TestBase {
  /**
   * generate values.
   */
  public TestOfferModel() {
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
  public void testOfferModelPage() throws InterruptedException {
    // Page initialisation

    OfferModels offerModel = PageFactory.initElements(this.getDriver(), OfferModels.class);

    // Open offer model Page
    offerModel.gotoListPage(driver);
    // Entering data
    offerModel.fillData(driver, data);
   
  }

}
