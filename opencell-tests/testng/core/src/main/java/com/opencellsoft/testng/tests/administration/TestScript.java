package com.opencellsoft.testng.tests.administration;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.administration.ScriptPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author HASSNAA MIFTAH
 *
 */
public class TestScript extends TestBase {
    /**
     * generate values.
     */
    public TestScript() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "SC_" + dataKey;
        data.put(Constants.CODE, str);
        
    }
    
    /**
     * test method .
     * 
     * @throws InterruptedException exception 
     */
    @Test
    public void testScriptPage() throws InterruptedException {
        /**
         * init test.
         */
        ScriptPage scriptPage = PageFactory.initElements(this.getDriver(), ScriptPage.class);
        /**
         * Go to scripts Page.
         */
        scriptPage.gotoListPage(driver);
        /**
         * Fill the new scriptInstances form.
         */
        scriptPage.fillForm(driver, data);

    }
}
