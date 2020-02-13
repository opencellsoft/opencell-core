package com.opencellsoft.testng.tests.administration;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.administration.JobInstancesPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * Test Job Instances.
 * @author Maria AIT BRAHIM.
 *
 */
public class TestJobInstances extends TestBase {
    
    /**
     * generate values.
     */
    public TestJobInstances() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "JOB_" + dataKey;
        
        data.put(Constants.CODE, str);
    }
    /**
     * create Job Instances.
     * @throws InterruptedException 
     */
    @Test
    public void createJobInstances() throws InterruptedException {
        
        /**
         * init test.
         */
        JobInstancesPage jobInstancesPage = PageFactory.initElements(this.getDriver(),
            JobInstancesPage.class); 
        
        /**
         * Go to JobInstances Page.
         */
        jobInstancesPage.gotoListPage(driver);
        /**
         * Fill the new JobInstances form.
         */
        jobInstancesPage.fillForm(driver,data);
    }
}
