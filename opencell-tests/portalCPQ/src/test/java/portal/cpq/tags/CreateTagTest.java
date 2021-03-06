package portal.cpq.tags;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
//Generated by Selenium IDE
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;

public class CreateTagTest {
	private WebDriver driver;
	private Map<String, Object> vars;
	JavascriptExecutor js;

	@BeforeMethod
	public void setUp() {
		driver = new FirefoxDriver();
		js = (JavascriptExecutor) driver;
		vars = new HashMap<String, Object>();
	}

	@AfterMethod
	public void tearDown() {
		driver.quit();
	}

	@Test
	public void createtag() {
		driver.get("https://cpq.d2.opencell.work/opencell/frontend/DEMO/mediapost/");
		driver.manage().window().setSize(new Dimension(1238, 659));
		driver.findElement(By.id("password")).sendKeys("opencell.admin");
		driver.findElement(By.id("username")).sendKeys("opencell.admin");
		driver.findElement(By.xpath("//input[@id=\'kc-login\']")).click();
		driver.findElement(
				By.xpath("//a[@class=\'MuiButtonBase-root MuiListItem-root MuiMenuItem-root jss115 jss66 MuiMenuItem-gutters MuiListItem-gutters MuiListItem-button jss116 jss63\']"))
				.click();
		driver.findElement(
				By.cssSelector(".MuiToolbar-root:nth-child(2) > .MuiToolbar-root .MuiSvgIcon-root"))
				.click();
		driver.findElement(By.id("code")).sendKeys("test");
		driver.findElement(By.id("name")).sendKeys("test");
		driver.findElement(By.id("description")).sendKeys("test");
		driver.findElement(By.xpath("//div[@id=\'seller.code\']")).click();
		driver.findElement(By.xpath("//li[contains(text(),\'MAIN_SELLER\')]"))
				.click();
		driver.findElement(By.xpath("//div[@id=\'tagType.code\']")).click();
		driver.findElement(By.xpath("//li[contains(text(),\'TagType_1\')]"))
				.click();
		driver.findElement(By.xpath("//div[@id=\'parentTag.code\']")).click();
		driver.findElement(By.xpath("//li[contains(text(),\'DIGITAL\')]"))
				.click();
		driver.findElement(By.xpath("//span[@class=\'MuiButton-label\']"))
				.click();
		{
			List<WebElement> elements = driver.findElements(By
					.xpath("//div[@class=\'MuiSnackbarContent-message\']"));
			assert (elements.size() > 0);
		}
		Assert.assertEquals(driver.findElement(
				By.xpath("//div[@class=\'MuiSnackbarContent-message\']"))
				.getText(), "Element created");
	}
}
