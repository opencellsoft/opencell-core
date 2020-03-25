/**
 * 
 */
package com.opencellsoft.testng.tests.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.util.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.AfterTest;

import com.opencellsoft.testng.pages.LoginPage;

/**
 * @author phung
 *
 */
public abstract class TestBase {

    /** LOGGER . */
    private static Logger LOGGER = Logger.getLogger(TestBase.class);

    /** date format by default . */
    protected static final String DATE_FORMAT = "MM/dd/yyyy";

    /** Operation system. */
    private static String OS = System.getProperty("os.name").toLowerCase();

    /** constant for value. */
    protected static final String ATTRIBUTE_VALUE = "value";

    /** time out for waiting element . */

    private static final long TIMEOUT = 10;

    /** time out for waiting element . */
    private static final long MAX_TIMEOUT = 10;

    /**MM timeout ?*/
    public static final long COMBO_TIMEOUT = 10;

    /** Web driver. */
    protected static WebDriver driver;

    /** admin url. */
    private static String adminURL;

    /** cc url. */
    private static String customerCareURL;
    
    /** sc url. */
    private static String selfCareURL;

    /** customer care username. */
    private static String ccUsername;

    /** self care username. */
    private static String scUsername;
    
    /** customer care password. */
    private static String ccPassword;
    
    /** self care password. */
    private static String scPassword;

    /** opencell GUI username. */
    private static String adminUsername;

    /** opencell GUI password. */
    private static String adminPassword;

    /** marketing mananger username. */
    private static String mmUsername;

    /** marketing manager password. */
    private static String mmPassword;

    /** date format.*/
    private static String dateFormat;
    
    /** timeout.*/
    private static long timeout;
    
    /** max timeout.*/
    private static long maxTimeout;

    /** map of infos. */
    protected final Map<String, String> data = new LinkedHashMap<>();

    /** map of index. */
    protected final Map<String, Integer> index = new LinkedHashMap<>();

    /** data key . */
    protected String dataKey;

    /** gecko driver path. */
    private static String geckoDirverPath = null;

    /** start browser headless or not. */
    private static boolean headlessOption = true;

    /** WebDriver URL. */
    private static String webdriverURL;
    
    /** properties from file.*/
    private static Properties props = new Properties();

    static {
        //https://github.com/mozilla/geckodriver/releases
        URL configFileURL = Thread.currentThread().getContextClassLoader().getResource("properties/config.properties");
        try {
            props.load(new FileInputStream(configFileURL.getPath()));
            // login for CC
            setCcUsername((String) props.get("cc.username"));
            setCcPassword((String) props.get("cc.password"));

            // login for Admin
            setAdminUsername((String) props.get("admin.username"));
            setAdminPassword((String) props.get("admin.password"));

            // login for MM
            setMmUsername((String) props.get("mm.username"));
            setMmPassword((String) props.get("mm.password"));

            setAdminURL((String) props.get("admin.url"));
            setCustomerCareURL((String) props.get("cc.url"));
            setSelfCareURL((String) props.get("sc.url"));
            // login for SC 
            //setScUsername((String) props.get("sc.username"));
            //setScPassword((String) props.get("sc.Password"));
            

	    setHeadlessOption(Boolean.parseBoolean((String) props.get("browser.headless")));
	    

            String timeoutFromFile = (String) props.get("timeout");
            if (timeoutFromFile != null && !"".equals(timeoutFromFile)) {
                try {
                    long parseLong = Long.parseLong(timeoutFromFile);
                    timeout = parseLong;
                } catch (Exception ex) {
                    timeout = TIMEOUT;
                }
            }

            setTimeout(timeout);

            String timeoutMaxFromFile = (String) props.get("max.timeout");
            if (timeoutMaxFromFile != null && !"".equals(timeoutMaxFromFile)) {
                try {
                    long parseLong = Long.parseLong(timeoutMaxFromFile);
                    maxTimeout = parseLong;
                } catch (Exception ex) {
                    maxTimeout = MAX_TIMEOUT;
                }
            }

            setMaxTimeout(maxTimeout);

            if (isWindows()) {
                geckoDirverPath = (String) props.get("geckodriver.windows.path");
                if (geckoDirverPath == null || "".equals(geckoDirverPath)) {
                    URL resource = Thread.currentThread().getContextClassLoader().getResource("drivers/geckodriver.exe");
                    geckoDirverPath = resource.getPath();
                }
            } else if (isUnix() || isSolaris()) {
                geckoDirverPath = (String) props.get("geckodriver.unix.path");
            } else if (isMac()) {
                geckoDirverPath = (String) props.get("geckodriver.mac.path");
            }
        } catch (IOException ex) {
            LOGGER.error("Error when reading properties file", ex);
        }


        System.setProperty("webdriver.gecko.driver", geckoDirverPath);
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setAcceptInsecureCerts(true);
        capabilities.setCapability("marionette", true);
	//Set Firefox Headless mode as TRUE
	FirefoxOptions options = new FirefoxOptions();
	options.setHeadless(headlessOption);
	capabilities.setCapability("moz:firefoxOptions", options); 
        try {
            driver = new RemoteWebDriver(new URL((String) props.get("webdriver.url")), capabilities);
        } catch (Exception e) {
            driver = new FirefoxDriver(options);
        }

        
        driver.manage().timeouts().implicitlyWait(getMaxTimeout(), TimeUnit.SECONDS);
        

    }

    /**
     * default constructor.
     */
    public TestBase() {
        /**
        geckodriver.windows.path =
        geckodriver.mac.path = /tmp/geckodriver_macos
        geckodriver.unix.path = /tmp/geckodriver_unix
        */
    }

    @BeforeMethod
    public void beforeMethod() {

    }
    /**
     * Login to core GUI.
     */
    public void corelogin() {
        this.getDriver().get(getAdminURL());
        LoginPage loginPage = PageFactory.initElements(this.getDriver(), LoginPage.class);
        loginPage.loginKC(getAdminUsername(), getAdminPassword());
    }

    /**
     * login to marketing manage page.
     */
    public void mmlogin() {
        this.getDriver().get(getAdminURL());
        LoginPage loginPage = PageFactory.initElements(this.getDriver(), LoginPage.class);
        loginPage.loginKC(getMmUsername(), getMmPassword());
    }

    /**
     * @param path
     *            file's path
     */
    protected void doPrintScreen(String path) {
        WebDriver augmentedDriver = new Augmenter().augment(driver);
        File screenshot = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshot, new File(path));
        } catch (IOException e) {
            LOGGER.error("Error when copying file", e);
        }
    }

    /**
     * protected void waitAndClick(WebDriver driver, By by, long waitTimeinMiliseconds) { WebDriverWait wait = new WebDriverWait(driver, waitTimeinMiliseconds);
     * driver.findElement(by).click(); wait.until(ExpectedConditions.visibilityOfElementLocated(by)).click(); }
     */

    /**
     * @param webElement
     *            web element to check.
     * @param driver
     *            web driver
     */
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

        driver.manage().timeouts().implicitlyWait(getMaxTimeout(), TimeUnit.SECONDS);
    }

    /**
     * @param data
     *            data to store.
     * @param fileName
     *            name of file to create
     */
    protected void serialize(Object data, String fileName) {

        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
            fos.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    /**
     * @param fileName
     *            name of file to read
     * @return found object.
     */
    protected Object deserialize(String fileName) {
        Object foundObject = null;
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            foundObject = ois.readObject();
            ois.close();
            fis.close();

        } catch (IOException | ClassNotFoundException ioe) {
            ioe.printStackTrace();
        }

        return foundObject;

    }

    /**
     * @param webElement
     *            element of the page.
     * @param driver
     *            web driver.
     */
    public void waitElementToDisplayAndDisapear(final WebElement webElement, WebDriver driver) {
        waitUntilElementDisplayed(webElement, driver);
    }

    /**
     * @param webElement
     *            web element to check
     * @param driver
     *            web driver
     */
    @SuppressWarnings("unchecked")
    public void waitUntilElementNotDisplayed(final WebElement webElement, WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, getTimeout());
        @SuppressWarnings("rawtypes")
        ExpectedCondition elementIsDisplayed = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver arg0) {
                try {
                    webElement.isDisplayed();
                    return false;
                } catch (NoSuchElementException e) {
                    return true;
                } catch (StaleElementReferenceException f) {
                    return true;
                }
            }
        };
        wait.until(elementIsDisplayed);
    }

    /**
     * @param webElement
     *            web element to check
     * @param driver
     *            web driver
     */
    public void waitUntilElementClickable(final WebElement webElement, WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, getTimeout());
        wait.until(ExpectedConditions.elementToBeClickable(webElement));
    }

    /**
     * @param driver
     *            web driver.
     * @param locator
     *            locator for element.
     * @return true/false
     */
    protected boolean isElementPresent(WebDriver driver, By locator) {
        // Set the timeout to something low
        driver.manage().timeouts().implicitlyWait(getMaxTimeout(), TimeUnit.MILLISECONDS);

        try {
            driver.findElement(locator);
            // If element is found set the timeout back and return true
            driver.manage().timeouts().implicitlyWait(getMaxTimeout(), TimeUnit.SECONDS);
            return true;
        } catch (NoSuchElementException ex) {
            // If element isn't found, set the timeout and return false
            driver.manage().timeouts().implicitlyWait(getMaxTimeout(), TimeUnit.SECONDS);
            return false;
        }
    }
    
    /**
     * @param element 
     */
    public void forceClick(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }

    /**
     * method called when test is finished.
     */
    @AfterMethod
    protected void afterMethod() {
        //doPrintScreen("C:\\tmp\\test.png");
    }

    /**
     * called after test suite.
     */
    @AfterSuite
    protected void afterSuite() {
        //driver.quit();
    }

    /**
     * called before test suite.
     * @param language language to run test.
     */
    @BeforeSuite
    @Parameters({ "language" })
    protected void beforeSuite(String language) {
        if (language != null && !"".equals(language)) {
            setDateFormat((String) props.get("dateFormat." + language));
        } else {
            setDateFormat(DATE_FORMAT);
        }
    }

    /**
     * called before each <test/>.
     */
    @BeforeTest
    protected void beforeTest() {

    }

    /**
     * called after each <test/>.
     */
    @AfterTest
    protected void afterTest() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * @return true if OS is windows.
     */
    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    /**
     *@return true if OS is mac
     */
    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    /**
     * @return true if OS is unix/linux
     */
    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);

    }

    /**
     * @return true if OS is solaris
     */
    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }

    /**
     * @return web driver.
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * @return the customerCareURL
     */
    public static String getCustomerCareURL() {
        return customerCareURL;
    }
    public static String getSelfCareURL() {
        return selfCareURL;
    }

    /**
     * @param customerCareURL
     *            the customerCareURL to set
     */
    public static void setCustomerCareURL(String customerCareURL) {
        TestBase.customerCareURL = customerCareURL;
    }

    /**
     * @return the adminURL
     */
    public static String getAdminURL() {
        return adminURL;
    }

    /**
     * @param adminURL
     *            the adminURL to set
     */
    public static void setAdminURL(String adminURL) {
        TestBase.adminURL = adminURL;
    }

    /**
     * @return the ccUsername
     */
    public static String getCcUsername() {
        return ccUsername;
    }

    /**
     * @param ccUsername the ccUsername to set
     */
    public static void setCcUsername(String ccUsername) {
        TestBase.ccUsername = ccUsername;
    }

    /**
     * @return the ccPassword
     */
    public static String getCcPassword() {
        return ccPassword;
    }

    /**
     * @param ccPassword the ccPassword to set
     */
    public static void setCcPassword(String ccPassword) {
        TestBase.ccPassword = ccPassword;
    }

    /**
     * @return the adminUsername
     */
    public static String getAdminUsername() {
        return adminUsername;
    }

    /**
     * @param adminUsername the adminUsername to set
     */
    public static void setAdminUsername(String adminUsername) {
        TestBase.adminUsername = adminUsername;
    }

    /**
     * @return the adminPassword
     */
    public static String getAdminPassword() {
        return adminPassword;
    }

    /**
     * @param adminPassword the adminPassword to set
     */
    public static void setAdminPassword(String adminPassword) {
        TestBase.adminPassword = adminPassword;
    }

    /**
     * @return the mmUsername
     */
    public static String getMmUsername() {
        return mmUsername;
    }

    /**
     * @param mmUsername the mmUsername to set
     */
    public static void setMmUsername(String mmUsername) {
        TestBase.mmUsername = mmUsername;
    }

    /**
     * @return the mmPassword
     */
    public static String getMmPassword() {
        return mmPassword;
    }

    /**
     * @param mmPassword the mmPassword to set
     */
    public static void setMmPassword(String mmPassword) {
        TestBase.mmPassword = mmPassword;
    }

    /**
     * @return the dateFormat
     */
    public static String getDateFormat() {
        return dateFormat;
    }

    /**
     * @param dateFormat the dateFormat to set
     */
    public static void setDateFormat(String dateFormat) {
        TestBase.dateFormat = dateFormat;
    }

    /**
     * @return the timeout
     */
    public static long getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public static void setTimeout(long timeout) {
        TestBase.timeout = timeout;
    }

    /**
     * @return the maxTimeout
     */
    public static long getMaxTimeout() {
        return maxTimeout;
    }

    /**
     * @param maxTimeout the maxTimeout to set
     */
    public static void setMaxTimeout(long maxTimeout) {
        TestBase.maxTimeout = maxTimeout;
    }


    /**
     * @param bool to set headlessOption
     */
    public static void setHeadlessOption(boolean headlessOption) {
        TestBase.headlessOption = headlessOption;
    }

    /**
     * @return the webdriver URL 
     */
    public static String getWebdriverURL() {
        return webdriverURL;
    }

    /**
     * @return the scUsername
     */
    
    public static String getScUsername() {
        return scUsername;
    }

    /**
     * @param scUsername the scUsername to set
     */
    public static void setScUsername(String scUsername) {
        TestBase.scUsername = scUsername;
    }

    /**
     * @return the scPassword
     */
    public static String getScPassword() {
        return scPassword;
    }

    /**
     * @param scPassword the scPassword to set
     */
    public static void setScPassword(String scPassword) {
        TestBase.scPassword = scPassword;
    }

    /**
     * @param selfCareURL the selfCareURL to set
     */
    public static void setSelfCareURL(String selfCareURL) {
        TestBase.selfCareURL = selfCareURL;
    }



}
