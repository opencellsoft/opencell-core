package com.opencellsoft.testng.pages.configuration;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class InvoiceSepPage extends BasePage {
	/**
	 * button new.
	 */
	@FindBy(id = "searchForm:buttonNew")
	private WebElement btnNew;
	/**
	 * invoiceSequencecode.
	 */
	@FindBy(id = "formInvoiceSequence:code_txt")
	private WebElement invoiceSequencecode;
	/**
	 * saveButton .
	 */
	@FindBy(id = "formInvoiceSequence:formButtonsCC:saveButton")
	private WebElement saveButton;

	public InvoiceSepPage(WebDriver driver) {
		super(driver);
	}

	public void gotoListPage(WebDriver driver) {
		WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
		moveMouse(configurationMenu);

		WebElement facturation = driver.findElement(By.id("menu:invoicingconfig"));
		moveMouse(facturation);

		WebElement invoiceSequences = driver.findElement(By.id("menu:invoiceSequences"));
		moveMouseAndClick(invoiceSequences);
	}

	public void goTobtnNew(WebDriver driver) throws InterruptedException {
	    moveMouseAndClick(btnNew);
	}

	public void fillData(WebDriver driver, Map<String, String> data) throws InterruptedException {
	    moveMouseAndClick(invoiceSequencecode);
		invoiceSequencecode.clear();
		invoiceSequencecode.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(saveButton);

	}

}
