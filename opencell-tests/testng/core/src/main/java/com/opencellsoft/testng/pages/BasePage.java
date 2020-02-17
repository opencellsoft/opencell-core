/**
 * 
 */
package com.opencellsoft.testng.pages;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author phung
 *
 */
public class BasePage {

    /** index .*/
    protected static int index = 1;

    /**
     * loader popup.
     */
    @FindBy(className = "modal-dialog")
    private WebElement customerCareLoaderModal;


    /**
     * web driver.
     */
    private WebDriver driver;
    private static final long TIMEOUT =20;

    private static final long MAX_TIMEOUT = 40;
    /**
     * default constructor.
     * 
     * @param driver
     */
    public BasePage(WebDriver driver) {
        this.setDriver(driver);
    }

    /**
     * @return the driver
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * @param driver the driver to set
     */
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * @return the customerCareLoaderModal
     */
    public WebElement getCustomerCareLoaderModal() {
        return customerCareLoaderModal;
    }

    /**
     * @return the index
     */
    public static int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public static void setIndex(int index) {
        BasePage.index = index;
    }

    /**
     * @return the timeout
     */
    public static long getTimeout() {
        return TIMEOUT;
    }

    /**
     * @return the maxTimeout
     */
    public static long getMaxTimeout() {
        return MAX_TIMEOUT;
    }

    /**
     * @param customerCareLoaderModal the customerCareLoaderModal to set
     */
    public void setCustomerCareLoaderModal(WebElement customerCareLoaderModal) {
        this.customerCareLoaderModal = customerCareLoaderModal;
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void waitUntilElementDisplayed(final WebElement webElement, WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        WebDriverWait wait = new WebDriverWait(driver, getTimeout());
        ExpectedCondition elementIsDisplayed = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver arg0) {
                try {
                    webElement.isDisplayed();
                    return true;
                } catch (NoSuchElementException e) {
                    return false;
                } catch (StaleElementReferenceException f) {
                    return false;
                }
            }
        };

        wait.until(elementIsDisplayed);
        
        //wait.until(ExpectedConditions.visibilityOf(webElement));
       // wait.until(ExpectedConditions.stalenessOf(webElement));

        driver.manage().timeouts().implicitlyWait(getMaxTimeout(), TimeUnit.SECONDS);
    }
    /**
     * @param element 
     */
    public void forceClick(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }

    /**
     * @param webElement web element.
     */
    public void moveMouse(WebElement webElement) {
        //Actions action = new Actions(driver);
        //action.moveToElement(webElement).perform();
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", webElement);
    }

    /**
     * @param webElement web element.
     * @param nextWebElement next web element to move
     */
    public void moveMouse(WebElement webElement, WebElement nextWebElement) {
        Actions action = new Actions(driver);
        action.moveToElement(webElement).moveToElement(nextWebElement).build().perform();
    }
    
    /**
     * @param webElement web element
     * @param nextWebElement next element
     * @param afterNextElement another element.
     */
    public void moveMouse(WebElement webElement, WebElement nextWebElement, WebElement afterNextElement) {
        Actions action = new Actions(driver);
        action.moveToElement(webElement).moveToElement(nextWebElement).moveToElement(afterNextElement).build().perform();
    }
    /**
     * Moves mouse the give element and click.
     * @param webElement web element
     */
    public void moveMouseAndClick(WebElement webElement) {
        //Actions action = new Actions(driver);
        //action.moveToElement(webElement).click().build().perform();
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", webElement);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", webElement);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // Use logger here.
        }


    }

}
