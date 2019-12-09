package com.opencellsoft.testng.tests.configuration;

import static org.testng.Assert.assertEquals;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.configuration.EmailTemplatePage;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * 
 * @author Maria AIT BRAHIM
 */
public class TestEmailTemplates extends TestBase {

    /**
     * fill the constants.
     */
    public TestEmailTemplates() {
        String test = "RE_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);
        data.put(Constants.SUBJECT, test);

    }

    /**
     * email Templates Test.
     * @throws InterruptedException 
     */

    @Test
    private void testEmailTemplatePage() throws InterruptedException {
        EmailTemplatePage emailTemplatePage = PageFactory.initElements(this.getDriver(),
            EmailTemplatePage.class);
        emailTemplatePage.gotoListPage(driver);
        emailTemplatePage.gotoNewPage(driver);
        emailTemplatePage.fillFormCreate(driver, data);
        //testData(emailTemplatePage);
        emailTemplatePage.saveEmail(driver);
        emailTemplatePage.fillFormAndSearch(driver, data);
        emailTemplatePage.delete(driver);
    }

    /**
     * Check the mandatory fields.
     * 
     * @param page instance of EmailTemplate .
     */
    private void testData(EmailTemplatePage page) {
        String code = page.getCodeEt().getAttribute(ATTRIBUTE_VALUE);
        String subject = page.getSubjectEt().getAttribute(ATTRIBUTE_VALUE);

        assertEquals(code, data.get(Constants.CODE));
        assertEquals(subject, data.get(Constants.SUBJECT));

    }

}
