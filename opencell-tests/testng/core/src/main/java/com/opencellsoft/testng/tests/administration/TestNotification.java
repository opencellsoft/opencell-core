package com.opencellsoft.testng.tests.administration;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.administration.Notification;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * notification test.
 * 
 * @author MIFTAH
 *
 */
public class TestNotification extends TestBase {
  /**
   * generate values.
   */
  public TestNotification() {
    dataKey = String.valueOf(System.currentTimeMillis());
    String str = "Not_" + dataKey;

    data.put(Constants.CODE, str);
    data.put(Constants.DESCRIPTION, str);

  }

  /**
   * Offer Model.
   */
  @Test
  /**
   * test notification page time.
   * 
   * @throws InterruptedException Exception
   */
  public void testNotificationPage() throws InterruptedException {
    // Page initialisation

    Notification notification = PageFactory.initElements(this.getDriver(), Notification.class);
    

    // Open offer model Page
    notification.gotoListPage(driver);

    // Entering new offer model
    notification.goTobtnNew(driver);

    // Entering data
    notification.fillData(driver, data);

    // deleting data
    notification.delete(driver, data);
  }
}
