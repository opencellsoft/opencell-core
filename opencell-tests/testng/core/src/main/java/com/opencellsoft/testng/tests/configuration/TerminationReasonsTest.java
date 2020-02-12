package com.opencellsoft.testng.tests.configuration;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.configuration.TerminationReasons;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * @author Fatine BELHADJ
 *
 */
public class TerminationReasonsTest extends TestBase {
    /**
     * Default constructor.
     */
    public TerminationReasonsTest() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "TC_" + dataKey;

        data.put(Constants.CODE, str);
        data.put(Constants.DESCRIPTION, str);

    }

    /**
     * create termination reasons test.
     */
    @Test
    public void create() {
        /**
         * Page initialisation.
         */
        TerminationReasons terminationreasons = PageFactory.initElements(this.getDriver(),
            TerminationReasons.class);

        /** Open Terminationreason Page */
        terminationreasons.gotoListPage(driver);

        /** Entering new contact */
        terminationreasons.goTobtnNew(driver);

        /** Entering data */
        terminationreasons.fillData(driver, data);

        /** Saving data */
        terminationreasons.goToSave(driver);

        /** Searching and deleting data */
        terminationreasons.fillAndSearche(driver, data);
        terminationreasons.delete(driver);

    }

}
