package com.opencellsoft.testng.tests.administration;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.administration.TimersPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author ADMIN
 *
 */
public class TestTimers extends TestBase {
    /**
     * generate values.
     */
    public TestTimers() {
      dataKey = String.valueOf(System.currentTimeMillis());
      String str = "TI_" + dataKey;

      data.put(Constants.CODE, str);
      data.put(Constants.DESCRIPTION, str);

    }
    /**
     * create method.
     * @throws InterruptedException 
     */
    @Test
    public void create() throws InterruptedException {
        
        /**
         * init test.
         */
        TimersPage timersPage = PageFactory.initElements(this.getDriver(), TimersPage.class);
        /**
         * Go to timers Page.
         */
        
        timersPage.gotoListPage(driver);
        /**
         * Fill the new timers  form.
         */
        timersPage.fillForm(driver, data);
        /**
         * testData.
         */
        testData(timersPage);
        /**
         * Save the new timers .
         */
        timersPage.gotoSave(driver);
        
        timersPage.searchAndDelete(driver, data);
    }
    /**
     * Check the mandatory fields.
     * 
     * @param timersPage instance of OpenCellInstancesPage .
     */
    private void testData(TimersPage timersPage) {
      String code = timersPage.getCode().getAttribute(ATTRIBUTE_VALUE);
      String description = timersPage.getDescription().getAttribute(ATTRIBUTE_VALUE);

      assertEquals(code, data.get(Constants.CODE));
      assertEquals(description, data.get(Constants.DESCRIPTION));

    }

}
