package com.opencellsoft.testng.tests.setup;

import static org.testng.Assert.assertEquals;

import com.opencellsoft.testng.pages.Constants;

import com.opencellsoft.testng.pages.setup.AssuredFactorPage;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * 
 * @author Maria AIT BRAHIM
 */
public class TestAssuredFactor extends TestBase {

    /**
     * fill the constants.
     */
    public TestAssuredFactor() {
        String test = "AF_" + System.currentTimeMillis();

        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        
    }

    /**
     * Assured Factor  test.
     * @throws InterruptedException 
     */

    @Test
    private void testAssuredFactor() throws InterruptedException {
        AssuredFactorPage assuredFactorPage = PageFactory
            .initElements(this.getDriver(), AssuredFactorPage.class);
        assuredFactorPage.gotoListPage(driver);
        assuredFactorPage.gotoNewPage(driver);
        assuredFactorPage.fillFormCreate(driver, data);
        testData(assuredFactorPage);
        assuredFactorPage.saveOperation(driver);
       
    }

    /**
     * Check mandatory fields.
     * 
     * @param page instance of Assured Factors 
     */
    private void testData(AssuredFactorPage page) {
        String code = page.getCodeAF().getAttribute(ATTRIBUTE_VALUE);
        String description = page.getDescriptionAF().getAttribute(ATTRIBUTE_VALUE);
      

        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));
       
    }

}
