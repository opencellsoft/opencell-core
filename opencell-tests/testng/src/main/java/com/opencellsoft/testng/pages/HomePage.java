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
public class HomePage extends BasePage {

    /** Provider menu. */
    @FindBy(id = "menu:admin")
    public static WebElement congfigurationMenu;

    /**
     * application menu.
     */
    @FindBy(id = "menu:provider")
    public static WebElement providerMenu;

    /**
     * @param driver
     */
    public HomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * @return enter user name.
     */
    public HomePage clickConfigurationMenu() {
        congfigurationMenu.click();
        providerMenu.click();
        return PageFactory.initElements(this.getDriver(), HomePage.class);
    }
}
