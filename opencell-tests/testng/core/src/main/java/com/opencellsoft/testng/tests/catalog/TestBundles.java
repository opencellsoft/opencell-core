package com.opencellsoft.testng.tests.catalog;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.BundlesPage;
import com.opencellsoft.testng.tests.base.TestBase;


public class TestBundles extends TestBase{
	/**
     * generate values.
     */
    public static int compteur = 0;
    public TestBundles() {
      dataKey = String.valueOf(System.currentTimeMillis());
      String str = "OF_" + dataKey;

      data.put(Constants.CODE, str);

    }
    @Test
    public void createBundles() throws InterruptedException {
    BundlesPage bundlesPage = PageFactory.initElements(this.getDriver(), BundlesPage.class);
    
    bundlesPage.gotoListPage(driver);
    bundlesPage.fillInformationsForm(driver,data);
    
    } 
}
