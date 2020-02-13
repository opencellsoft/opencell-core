package com.opencellsoft;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.LoginPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author phung
 *
 */
public abstract class TestLoginKC extends TestBase {

    @Test
    public void login() {
        this.getDriver().get(getLoginUrl());
        LoginPage loginPage = PageFactory.initElements(this.getDriver(), LoginPage.class);
        loginPage.loginKC(getUserName(), getPassword());

        waitUntilElementNotDisplayed(loginPage.getCustomerCareLoaderModal(), driver);
    }

    /**
     * @return login url
     */
    protected abstract String getLoginUrl();

    /**
     * @return username
     */
    protected abstract String getUserName();

    /**
     * @return password
     */
    protected abstract String getPassword();

}
