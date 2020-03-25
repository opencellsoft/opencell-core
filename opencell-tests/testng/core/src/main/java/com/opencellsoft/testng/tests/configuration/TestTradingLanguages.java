package com.opencellsoft.testng.tests.configuration;

import com.opencellsoft.testng.pages.configuration.TradingLanguagesPage;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * @author Fatine BELHADJ
 *
 */
public class TestTradingLanguages extends TestBase {
    /**
     * create trading language test.
     * @throws InterruptedException 
     */
    @Test
    public void create() throws InterruptedException {
        /**
         * Page initialisation.
         */
        TradingLanguagesPage tradinglanguagespage = PageFactory.initElements(this.getDriver(),
            TradingLanguagesPage.class);
        
        /** Open Trading Language Page */
        tradinglanguagespage.gotoListPage(driver);
        

        tradinglanguagespage.languageSelect(driver);
        tradinglanguagespage.delete(driver);

    }

}
