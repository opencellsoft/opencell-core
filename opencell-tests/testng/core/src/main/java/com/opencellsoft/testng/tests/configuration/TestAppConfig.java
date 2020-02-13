package com.opencellsoft.testng.tests.configuration;

import com.opencellsoft.testng.pages.configuration.AppConfigPage;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * 
 * @author Hassnaa Miftah
 *
 */
public class TestAppConfig extends TestBase {

    /**
     * create Test AppConfig.
     */
    @Test
    public void testAppConfigPage() {

        /**
         * init test
         */
        AppConfigPage appconfigPage = PageFactory.initElements(this.getDriver(),
            AppConfigPage.class);

        /**
         * Go to AppConfig Page
         */

        appconfigPage.gotoListPage(driver);

        /**
         * Fill the new AppConfig form
         */
        appconfigPage.fillForm(driver);

    }
}
