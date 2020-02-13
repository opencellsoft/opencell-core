package com.opencellsoft.testng.tests.administration;

import static org.testng.Assert.assertEquals;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.administration.WebhooksPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestWebhooks extends TestBase {
    /**
     * fill the constants.
     */
    public TestWebhooks() {
        String test = "WH_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        
    }
    
    /**
     * Webhooks Test.
     * 
     * @throws InterruptedException Exception
     */
    
    @Test
    private void testWebhooksPage() throws InterruptedException {
        WebhooksPage webhooksPage = PageFactory.initElements(this.getDriver(), WebhooksPage.class);
        webhooksPage.gotoListPage(driver);
        webhooksPage.gotoNewPage(driver);
        webhooksPage.fillFormCreate(driver, data);
        testData(webhooksPage);
        webhooksPage.save(driver);
        webhooksPage.searchWebHookAndDelete(driver, data);
    }
    
    /**
     * Check the mandatory fields.
     * 
     * @param webhooksPage instance of Filter .
     */
    private void testData(WebhooksPage webhooksPage) {
        String code = webhooksPage.getWebHookCode().getAttribute(ATTRIBUTE_VALUE);
        String host = webhooksPage.getHost().getAttribute(ATTRIBUTE_VALUE);
        
        assertEquals(code, data.get(Constants.CODE));
        assertEquals(host, data.get(Constants.DESCRIPTION));
        
    }
}
