/**
 * 
 */
package com.opencellsoft.testng.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * @author phung
 *
 */
public class LoginPage extends BasePage {

    /** user name field. */
    @FindBy(id = "username")
    private static WebElement usernameIpt;

    /** pass word field. */
    @FindBy(id = "password")
    private static WebElement passwordIpt;

    /** login button. */
    @FindBy(id = "kc-login")
    private static WebElement loginBt;

    /** user name . */
    private String username;

    /** password. */
    private String password;

    /**
     * default constructor.
     * 
     * @param driver
     *            web driver.
     */
    public LoginPage(WebDriver driver) {
        super(driver);

    }

    /**
     * @param username user name.
     * @param password
     * @return instance of LoginPage
     */
    public LoginPage loginKC(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
        usernameIpt.click();
        usernameIpt.clear();
        usernameIpt.sendKeys(this.getUsername());

        passwordIpt.click();
        passwordIpt.clear();
        passwordIpt.sendKeys(this.getPassword());
        passwordIpt.submit();

        // loginBt.click();

        return PageFactory.initElements(this.getDriver(), LoginPage.class);
    }
    

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

}
