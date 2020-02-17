package com.opencellsoft.testng.pages.customers;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class UserAccountPage extends BasePage{

	public UserAccountPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span")
	private WebElement buttonNew;

	@FindBy(xpath="/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[1]/div[1]/div/div/div")
	private WebElement billingAccountList ;

	@FindBy(xpath="/html/body/div[3]/div[2]/ul/li[3]")
	private WebElement billingAccount ;
	
	@FindBy(xpath="/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[1]/div[2]/div/input")
	private WebElement billingAccountCode ;
	
	@FindBy(xpath="/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[2]/span[1]/span/span")
	private WebElement contactTab;
	
	@FindBy(xpath="/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[3]/span[1]/span/span")
	private WebElement adressTab;
	@FindBy(xpath="/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[2]/div/div[2]/div[1]/div/input")
	private WebElement email ;
	
	@FindBy(xpath="/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div[1]/div[1]/div/input")
	private WebElement adress1 ;
	
	@FindBy(xpath="/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div[2]/div[1]/div/input")
	private WebElement zipCode ;
	
	@FindBy(xpath="/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div[2]/div[2]/div/input")
	private WebElement city ;
	@FindBy(xpath="/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div[2]/div[3]/div/input")
	private WebElement country ; 
	
	@FindBy(xpath="/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button")
	private WebElement saveBtn;
	
	@FindBy(xpath="/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[1]/div[1]/div[1]/div/input")
	private WebElement elementTodelete; 
	
	@FindBy(xpath="/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr/td[2]/span")
	private WebElement rowTodelelete;
	
	@FindBy(xpath="/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div[1]/div[2]/button/span[1]/span")
	private WebElement deleteBtn;
	
	@FindBy(xpath="/html/body/div[3]/div[2]/div[2]/button[2]/span[1]")
	private WebElement confirmDelete; 
	
	public void gotoListPage(WebDriver driver) {
		WebElement customersMenu = driver
				.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[1]/span[2]"));
		moveMouse(customersMenu);

		WebElement userAccounts = driver.findElement(
				By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[2]/div/div/a[4]"));
		moveMouseAndClick(userAccounts);

	}
	public void fillFormUserAccount(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(buttonNew);
		moveMouseAndClick(billingAccountList);
		moveMouseAndClick(billingAccount);
		moveMouseAndClick(billingAccountCode);
		billingAccountCode.clear();
		billingAccountCode.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(email);
		moveMouseAndClick(contactTab);
		email.clear();
		email.sendKeys("test.test@gmail.com");
		moveMouseAndClick(adressTab);
		moveMouseAndClick(adress1);
		adress1.clear();
		adress1.sendKeys("adress adress");
		moveMouseAndClick(zipCode);
		zipCode.clear();
		zipCode.sendKeys("123456");
		moveMouseAndClick(city);
		city.clear();
		city.sendKeys("Paris");
		moveMouseAndClick(country);
		country.clear();
		country.sendKeys("France");
		moveMouseAndClick(saveBtn);
		moveMouseAndClick(elementTodelete);
		elementTodelete.clear();
		elementTodelete.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(rowTodelelete);
		moveMouseAndClick(deleteBtn);
		moveMouseAndClick(confirmDelete);
	}
}
