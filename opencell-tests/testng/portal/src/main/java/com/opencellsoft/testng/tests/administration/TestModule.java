package com.opencellsoft.testng.tests.administration;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.administration.ModulePage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author MIFTAH
 */
public class TestModule extends TestBase  {

  /**
   * fill the constants.
   */
  public TestModule() {
    String test = "MOD_" + System.currentTimeMillis();
    data.put(Constants.CODE, test);
    data.put(Constants.DESCRIPTION, test);

  }

  /**
   * Module Test.
   * 
   * @throws InterruptedException Exception
   */

  @Test
  private void testModulePage() throws InterruptedException {
    ModulePage modulePage = PageFactory.initElements(this.getDriver(), ModulePage.class);
    modulePage.gotoListPage(driver);
    modulePage.gotoNewPage(driver);
    modulePage.fillFormCreate(driver, data);
    testData(modulePage);
    modulePage.save(driver);
   
  }

  /**
   * Check the mandatory fields.
   * 
   * @param page instance of module .
   */
  private void testData(ModulePage page) {
    String code = page.getCode().getAttribute(ATTRIBUTE_VALUE);
    String description = page.getDescription().getAttribute(ATTRIBUTE_VALUE);

    assertEquals(code, data.get(Constants.CODE));
    assertEquals(description, data.get(Constants.DESCRIPTION));

  }

}
