package com.opencellsoft.testng.pages.catalog;

import org.openqa.selenium.WebDriver;

import com.opencellsoft.testng.pages.BasePage;

/**
 * @author Edward P. Legaspi
 * 
 */
public abstract class ChargeTemplatePage extends BasePage {

    /**
     * one shot type subscription.
     */
    public static final String DD_ONESHOT_TYPE_SUBSCRIPTION = "SUBSCRIPTION";

    /**
     * rounding mode.
     */
    public static final String DD_ROUNDING_MODE_UP = "UP";

    /**
     * invoice sub category consumption.
     */
    public static final String DD_INVOICE_SUB_CATEGORY_CONSUMPTION = "2";

    /**
     * @param driver web driver.
     */
    public ChargeTemplatePage(WebDriver driver) {
        super(driver);
    }

}
