package com.opencellsoft.testng.tests.payments;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.payments.DDrStatePage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestDDrState extends TestBase {
    @Test
    public void testDDrStatePage() throws InterruptedException {
        // Page initialisation
        DDrStatePage dDrStatePage = PageFactory.initElements(this.getDriver(),
            DDrStatePage.class);
        dDrStatePage.opendDrBuilderList(driver);
        dDrStatePage.fillDDrBuildersAndSave(driver, data);
        
}
    
}
