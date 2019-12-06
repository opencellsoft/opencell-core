package com.opencellsoft.testng.tests.administration;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.administration.OpenCellInstancesPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author MIFTAH
 */
public class TestOpenCellInstances extends TestBase {

  /**
   * fill the constants.
   */
  public TestOpenCellInstances() {
    String test = "OI_" + System.currentTimeMillis();
    data.put(Constants.CODE, test);
    data.put(Constants.DESCRIPTION, test);

  }

  /**
   * OpenCell Instances Test.
   * 
   * @throws InterruptedException Exception
   */

  @Test
  private void testOpenCellInstancesPage() throws InterruptedException {
    OpenCellInstancesPage openCellInstancesPage = PageFactory.initElements(this.getDriver(), OpenCellInstancesPage.class);
    openCellInstancesPage.gotoListPage(driver);
    openCellInstancesPage.gotoNewPage(driver);
    openCellInstancesPage.fillFormCreate(driver, data);
    testData(openCellInstancesPage);
    openCellInstancesPage.save(driver);
    openCellInstancesPage.fillFormAndSearch(driver, data);
    openCellInstancesPage.delete(driver);
  }

  /**
   * Check the mandatory fields.
   * 
   * @param page instance of OpenCellInstancesPage .
   */
  private void testData(OpenCellInstancesPage page) {
    String code = page.getCode().getAttribute(ATTRIBUTE_VALUE);
    String description = page.getDescription().getAttribute(ATTRIBUTE_VALUE);

    assertEquals(code, data.get(Constants.CODE));
    assertEquals(description, data.get(Constants.DESCRIPTION));

  }

}
