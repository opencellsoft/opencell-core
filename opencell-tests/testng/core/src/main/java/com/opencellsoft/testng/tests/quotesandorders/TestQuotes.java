package com.opencellsoft.testng.tests.quotesandorders;

import static org.testng.Assert.assertEquals;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.quotesandorders.QuotesPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestQuotes extends TestBase {
    
    public TestQuotes() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "QU_" + dataKey;
        data.put(Constants.CODE, str);
        
    }
    
    @Test
    public void testQuotesPage() throws InterruptedException {
        // Page initialisation
        QuotesPage quotesPage = PageFactory.initElements(this.getDriver(), QuotesPage.class);
        quotesPage.openQuotesList();
        quotesPage.fillQuotes(driver, data);
        testData(quotesPage);
        quotesPage.saveQuotes(driver);
        quotesPage.searchQuotesAndDelete(driver, data);
        
    }
    
    /**
     * Check the mandatory fields.
     * 
     * @param page instance of Filter .
     */
    private void testData(QuotesPage page) {
        String code = page.getCodeQuote().getAttribute(ATTRIBUTE_VALUE);
        
        assertEquals(code, data.get(Constants.CODE));
        
    }
    
}
