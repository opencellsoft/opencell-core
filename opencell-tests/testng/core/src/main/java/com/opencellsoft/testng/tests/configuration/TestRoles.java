package com.opencellsoft.testng.tests.configuration;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.configuration.RolesPage;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;

import org.testng.annotations.Test;

/**
 * @author Fatine BELHADJ
 *
 */
public class TestRoles extends TestBase {
    /**
     * Roles test.
     */
    public TestRoles() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "TC_" + dataKey;

        data.put(Constants.CODE, str);
        data.put(Constants.DESCRIPTION, str);

    }

    /**
     * create roles test.
     */
    @Test
    public void testRolesPage() throws InterruptedException {
        /**
         * Page initialisation.
         */
        RolesPage rolespage = PageFactory.initElements(this.getDriver(), RolesPage.class);

        // Open roles Page
        rolespage.gotoListPage(driver);

        // Entering new role

        rolespage.fillData(driver, data);
        
    }

}
