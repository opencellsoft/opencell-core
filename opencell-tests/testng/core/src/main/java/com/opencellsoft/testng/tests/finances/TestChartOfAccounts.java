package com.opencellsoft.testng.tests.finances;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.finances.ChartOfAccountsPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Miftah
 *
 */
public class TestChartOfAccounts extends TestBase {
    
    /**
     * generate values.
     */
    public TestChartOfAccounts() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "TC_" + dataKey;
        
        data.put(Constants.CODE, str);
    }
    
    /**
     * ChartOfAccounts Model.
     */
    @Test
    /**
     * test Chart Of Accounts page time.
     * 
     * @throws InterruptedException Exception
     */
    public void testChartOfAccounts() throws InterruptedException {
        
        ChartOfAccountsPage chartOfAccount = PageFactory.initElements(this.getDriver(),
            ChartOfAccountsPage.class);
        chartOfAccount.opendDrBuilderList();
        chartOfAccount.fillChartOfAccountsAndSave(driver, data);
        chartOfAccount.searchChartOfAccountsAndDelete(driver, data);
        
    }
    
}
