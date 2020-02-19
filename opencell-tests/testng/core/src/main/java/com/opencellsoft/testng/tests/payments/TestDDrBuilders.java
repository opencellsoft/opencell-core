package com.opencellsoft.testng.tests.payments;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.payments.DDrBuildersPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestDDrBuilders extends TestBase {
    public TestDDrBuilders() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "TC_" + dataKey;
        data.put(Constants.CODE, str);

    }
    @Test
    public void testDDrBuildersPage() throws InterruptedException {
        // Page initialisation
        DDrBuildersPage dDrBuildersPage = PageFactory.initElements(this.getDriver(),
            DDrBuildersPage.class);
        dDrBuildersPage.opendDrBuilderList(driver);
        dDrBuildersPage.fillDDrBuildersAndSave(driver, data);
        dDrBuildersPage.searchDDrBuildersAndDelete(driver, data);
        
}
}
