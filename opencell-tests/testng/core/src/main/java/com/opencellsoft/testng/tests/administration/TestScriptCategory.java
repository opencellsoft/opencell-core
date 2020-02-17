package com.opencellsoft.testng.tests.administration;


import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.administration.ScriptCategoryPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * workflow test.
 * 
 * @author MIFTAH 
 *
 */
public class TestScriptCategory extends TestBase {
  /**
   * generate values.
   */
  public TestScriptCategory() {
    dataKey = String.valueOf(System.currentTimeMillis());
    String str = "SCat_" + dataKey;

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

    ScriptCategoryPage workflows = PageFactory.initElements(this.getDriver(), ScriptCategoryPage.class);

    // Open offer model Page
    workflows.gotoListPage(driver);
    // Entering new offer model
    workflows.goTobtnNew(driver);
    // Entering data
    workflows.fillData(driver, data);
    // Saving data
    workflows.goToSave(driver);
    // deleting data
    workflows.delete(driver,data);
    
  }

  
}
