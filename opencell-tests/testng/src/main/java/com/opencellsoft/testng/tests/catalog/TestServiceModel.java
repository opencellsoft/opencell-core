package com.opencellsoft.testng.tests.catalog;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.ServiceModel;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * Service models test.
 * 
 * @author HP Fatine Belhadj
 *
 */
public class TestServiceModel extends TestBase {
  /**
   * generate values.
   */
  public TestServiceModel() {
    dataKey = String.valueOf(System.currentTimeMillis());
    String str = "TC_" + dataKey;

    data.put(Constants.CODE, str);
    data.put(Constants.DESCRIPTION, str);

  }

  /**
   * Offer Model.
 * @throws InterruptedException 
   */
  @Test
  public void create() throws InterruptedException {
    // Page initialisation

    ServiceModel serviceModel = PageFactory.initElements(this.getDriver(), ServiceModel.class);

    // Open offer model Page
    serviceModel.gotoListPage(driver);

    // Entering data
    serviceModel.fillData(driver, data);
  }


}
