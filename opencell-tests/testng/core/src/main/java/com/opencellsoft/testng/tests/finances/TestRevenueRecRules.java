package com.opencellsoft.testng.tests.finances;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.finances.RevenueRecRulesPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Miftah
 *
 */
public class TestRevenueRecRules extends TestBase {
    /**
     * generate values.
     */
    public TestRevenueRecRules() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "TC_" + dataKey;
        
        data.put(Constants.CODE, str);
    }
    
    /**
     * RevenueRecRules Model.
     */
    @Test
    /**
     * test Revenue.Rec Rules page .
     * 
     * @throws InterruptedException Exception
     */
    public void testRevenueRecRules() throws InterruptedException {
        
        RevenueRecRulesPage revenueRecRules = PageFactory.initElements(this.getDriver(),
            RevenueRecRulesPage.class);
        revenueRecRules.openRevenueRecRulesList(driver);
        revenueRecRules.fillRevenueRecRulesAndSave(driver, data);
        revenueRecRules.searchRevenueRecRulesPageAndDelete(driver, data);
        
    }
    
}
