package com.opencellsoft.testng.tests.administration;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.administration.EmailNotificationsPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestEmailNotifications extends TestBase {
    /**
     * fill the constants.
     */
    public TestEmailNotifications() {
        String test = "Not_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);
        data.put(Constants.EMAIL, test);
        
    }
    /**
     * EmailNotifications Test.
     * 
     * @throws InterruptedException Exception
     */
    
    @Test
    private void testEmailNotificationsPage() throws InterruptedException {
        EmailNotificationsPage emailNotificationsPage = PageFactory.initElements(this.getDriver(), EmailNotificationsPage.class);
        emailNotificationsPage.gotoListPage(driver);
        emailNotificationsPage.gotoNewPage(driver,data);
        //testData(emailNotificationsPage);
    }
    
    /**
     * Check the mandatory fields.
     * 
     * @param webhooksPage instance of Filter .
     */
    private void testData(EmailNotificationsPage emailNotificationsPage) {
        String code = emailNotificationsPage.getEmailNotificationFormCode().getAttribute(ATTRIBUTE_VALUE);
        String email = emailNotificationsPage.getEmailSentFrom().getAttribute(ATTRIBUTE_VALUE);
        
        assertEquals(code, data.get(Constants.CODE));
        assertEquals(email, data.get(Constants.EMAIL));
        
    }
}
