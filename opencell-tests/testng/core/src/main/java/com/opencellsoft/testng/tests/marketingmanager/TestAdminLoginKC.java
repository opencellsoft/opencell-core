package com.opencellsoft.testng.tests.marketingmanager;

import com.opencellsoft.TestLoginKC;

/**
 * @author phung
 *
 */
public class TestAdminLoginKC extends TestLoginKC {

    /**
     * @see com.opencellsoft.testng.tests.base.TestBase#getLoginUrl()
     */
    @Override
    protected String getLoginUrl() {
       return getAdminURL();
    }

    /**
     * @see com.opencellsoft.TestLoginKC#getUserName()
     */
    @Override
    protected String getUserName() {
       return getAdminUsername();
    }

    /**
     * @see com.opencellsoft.TestLoginKC#getPassword()
     */
    @Override
    protected String getPassword() {
        return getAdminPassword();
    }
}
