package com.opencellsoft.testng.pages.customers;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class CustomerPage extends BasePage {

	public CustomerPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span")
	private WebElement buttonNew;

	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[1]/div[1]/div/input")
	private WebElement code;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[4]/div[1]/div/div/div")
	private WebElement customerCatList;

	@FindBy(xpath = "/html/body/div[3]/div[2]/ul/li[6]")
	private WebElement customerCat;

	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[4]/div[2]/div/div/div")
	private WebElement sellerList;

	@FindBy(xpath = "/html/body/div[3]/div[2]/ul/li[4]")
	private WebElement seller;

	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button")
	private WebElement saveBtn;

	public void gotoListPage(WebDriver driver) {
		WebElement customersMenu = driver
				.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[1]/span[2]"));
		moveMouse(customersMenu);

		WebElement customerMenu = driver.findElement(
				By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[2]/div/div/a[1]"));
		moveMouseAndClick(customerMenu);

	}

	public void fillFormCustomer(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(buttonNew);
		moveMouseAndClick(code);
		code.clear();
		code.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(customerCatList);
		moveMouseAndClick(customerCat);
		moveMouseAndClick(sellerList);
		moveMouseAndClick(seller);
		moveMouseAndClick(saveBtn);
		WebElement seachArea = driver.findElement(By.xpath(
				"/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[1]/div[1]/div[1]/div/input"));
		moveMouseAndClick(seachArea);
		seachArea.sendKeys((String) data.get(Constants.CODE));
		for (int i = 0; i < 1000; i++) {
			try

			{
				WebElement custToDelete = driver.findElement(By.xpath(
						"/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr/td[2]/span"));
				moveMouseAndClick(custToDelete);
				break;
			}

			catch (StaleElementReferenceException see)

			{
			}
		}

		WebElement deleteBtn = driver.findElement(By.xpath(
				"/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div[1]/div[2]/button/span[1]/span"));
		moveMouseAndClick(deleteBtn);

		WebElement confirmDelete = driver.findElement(By.xpath("/html/body/div[3]/div[2]/div[2]/button[2]/span[1]"));
		moveMouseAndClick(confirmDelete);
	}
}
