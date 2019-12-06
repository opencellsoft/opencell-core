package com.opencellsoft.testng.tests.workflow;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.Counters;
import com.opencellsoft.testng.pages.workflow.WorkflowPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestWorkflow  extends TestBase {
    /**
     * generate values.
     */
    public TestWorkflow() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "WF_" + dataKey;
        
        data.put(Constants.CODE, str);
        data.put(Constants.DESCRIPTION, str);
        
    }
    /**
     * counters test.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testWorkflowPage() throws InterruptedException {
        // Page initialisation
        
        WorkflowPage workflowPage = PageFactory.initElements(this.getDriver(), WorkflowPage.class);
        
        // Open workflow model Page
        workflowPage.gotoListPage(driver);
        // Entering data
        workflowPage.fillData(driver, data);
    }

}
