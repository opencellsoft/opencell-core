package com.opencellsoft.testng.tests.configuration;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.configuration.TranslationsPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Hassnaa MIFTAH
 *
 */
public class TestTranslations extends TestBase {
    
    /**
     * Test Translation.
     */
    @Test
    public void testTranslationPage() {
        
        /**
         * Init test
         */
        TranslationsPage translationsPage = PageFactory.initElements(this.getDriver(),
            TranslationsPage.class);
        /**
         * Go to Translation Page
         */
        translationsPage.gotoListPage(driver);
        /**
         * choose an object
         */
        translationsPage.chooseObject(driver);
        /**
         * fill the form on the popUp
         */
        translationsPage.fillPopUp(driver, data);
        /**
         * click on save
         */
        translationsPage.save(driver);
    }
}
