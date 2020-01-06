package com.opencellsoft.testng.tests.administration;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.administration.JobNotificationsPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestJobNotifications extends TestBase {
    /**
     * fill the constants.
     */
    public TestJobNotifications() {
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
        JobNotificationsPage jobNotificationsPage = PageFactory.initElements(this.getDriver(),JobNotificationsPage.class);
        jobNotificationsPage.gotoListPage(driver);
        jobNotificationsPage.gotoNewPage(driver);
        jobNotificationsPage.fillFormCreate(driver, data);
        testData(jobNotificationsPage);
        jobNotificationsPage.save(driver);
        jobNotificationsPage.searchNotifAndDelete(driver, data);
    }
    
    /**
     * Check the mandatory fields.
     * 
     * @param webhooksPage instance of Filter .
     */
    private void testData(JobNotificationsPage jobNotificationsPage) {
        String code = jobNotificationsPage.getJobNotificationCode().getAttribute(ATTRIBUTE_VALUE);
        assertEquals(code, data.get(Constants.CODE));
        
    }
    
}
