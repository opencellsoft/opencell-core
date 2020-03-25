package com.opencellsoft.testng.tests.configuration;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.configuration.UserGroupsPage;
import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * @author Fatine BELHADJ
 *
 */

public class TestUserGroup extends TestBase {
    /**
     * TestUserGroup.
     */
    public TestUserGroup() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "TC_" + dataKey;
        data.put(Constants.CODE, str);
        data.put(Constants.DESCRIPTION, str);
        data.put(Constants.CODE_CHILD, str + "1234");

    }

    /**
     * create user group test.
     */
    @Test
    public void create() {
        /**
         * Page initialisation.
         */
        UserGroupsPage usergroupspage = PageFactory.initElements(this.getDriver(),
            UserGroupsPage.class);

        // Open UserGroups Page
        usergroupspage.gotoListPage(driver);

        // Entering new root user
        usergroupspage.addUserRoot(driver);

        // Fill root user with data
        usergroupspage.fillUserRoot(driver, data);

        /** Save data */
        usergroupspage.btnSaveUser(driver);

        /** Adding a child user */
        usergroupspage.addChildUser(driver);

        /** Fill child user with data */
        usergroupspage.fillUserChild(driver, data);

        /** Save child data */
        usergroupspage.btnSaveUser(driver);

        /** delete data */
        usergroupspage.deleteUser(driver);
    }

}
