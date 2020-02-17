package com.opencellsoft.testng.tests.configuration;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.configuration.ContactPage;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * @author Fatine BELHADJ
 *
 */
public class TestContact extends TestBase {
    /**
     * Test contact.
     */
    public TestContact() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "TC_" + dataKey;

        data.put(Constants.CODE, str);
        data.put(Constants.DESCRIPTION, str);
        data.put(Constants.EMAIL, str + "@gmail.com");
        data.put(Constants.PHONE, str);
        data.put(Constants.GENERIC_EMAIL, str + "@gmail.com");
        data.put(Constants.MOBILE, str);

    }

    /**
     * create contact test.
     * @throws InterruptedException 
     */
    @Test
    public void create() throws InterruptedException {
        /**
         * Page initialisation.
         */
        ContactPage contactPage = PageFactory.initElements(this.getDriver(), ContactPage.class);

        // Open contact Page
        contactPage.gotoListPage(driver);

        // Entering data

        contactPage.fillFormAndSave(driver, data);

  

    }

}
