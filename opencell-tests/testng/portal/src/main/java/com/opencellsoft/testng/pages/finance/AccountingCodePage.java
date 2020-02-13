package com.opencellsoft.testng.pages.finance;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class AccountingCodePage extends BasePage {

	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span")
	private WebElement buttonNew;

	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[1]/div/div/div[1]/div/input")
	private WebElement accountingCode;

	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[1]/div/div/div[4]/div/div/div")
	private WebElement accountTypeList;

	@FindBy(xpath = "/html/body/div[4]/div[2]/ul/li[4]")
	private WebElement accountType;

	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[1]/div/div/div[5]/div/div/div")
	private WebElement accountViewTypeList;

	@FindBy(xpath = "/html/body/div[4]/div[2]/ul/li[1]")
	private WebElement accountViewType;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[2]/div/button")
	private WebElement saveBtn;

	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[1]/div[1]/div[1]/div/input")
	private WebElement searchCode;

	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[1]/td[2]/span")
	private WebElement recordTodelete;

	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[2]/div/button[2]/span[1]/span")
	private WebElement deleteBtn;

	@FindBy(xpath = "/html/body/div[4]/div[2]/div[3]/button[2]/span[1]")
	private WebElement confirmBtn;

	public AccountingCodePage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	public void gotoListPage(WebDriver driver) {
		WebElement financeMenu = driver
				.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[3]/span[2]"));
		moveMouse(financeMenu);

		WebElement accountingCodeMenu = driver
				.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[4]/div/div/a"));
		moveMouseAndClick(accountingCodeMenu);

	}

	public void fillFormAccountCode(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(buttonNew);
		moveMouseAndClick(accountingCode);
		accountingCode.clear();
		accountingCode.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(accountTypeList);
		moveMouseAndClick(accountType);
		moveMouseAndClick(accountViewTypeList);
		moveMouseAndClick(accountViewType);
	}

	public void saveAccountingCode(WebDriver driver) throws InterruptedException {
		moveMouseAndClick(saveBtn);
	}

	public void searchAndDeleteAccountCode(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(searchCode);
		searchCode.clear();
		searchCode.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(recordTodelete);
		moveMouseAndClick(deleteBtn);
		moveMouseAndClick(confirmBtn);

	}

	/**
	 * @return the buttonNew
	 */
	public WebElement getButtonNew() {
		return buttonNew;
	}

	/**
	 * @param buttonNew the buttonNew to set
	 */
	public void setButtonNew(WebElement buttonNew) {
		this.buttonNew = buttonNew;
	}

	/**
	 * @return the accountingCode
	 */
	public WebElement getAccountingCode() {
		return accountingCode;
	}

	/**
	 * @param accountingCode the accountingCode to set
	 */
	public void setAccountingCode(WebElement accountingCode) {
		this.accountingCode = accountingCode;
	}

	/**
	 * @return the accountTypeList
	 */
	public WebElement getAccountTypeList() {
		return accountTypeList;
	}

	/**
	 * @param accountTypeList the accountTypeList to set
	 */
	public void setAccountTypeList(WebElement accountTypeList) {
		this.accountTypeList = accountTypeList;
	}

	/**
	 * @return the accountType
	 */
	public WebElement getAccountType() {
		return accountType;
	}

	/**
	 * @param accountType the accountType to set
	 */
	public void setAccountType(WebElement accountType) {
		this.accountType = accountType;
	}

	/**
	 * @return the accountViewTypeList
	 */
	public WebElement getAccountViewTypeList() {
		return accountViewTypeList;
	}

	/**
	 * @param accountViewTypeList the accountViewTypeList to set
	 */
	public void setAccountViewTypeList(WebElement accountViewTypeList) {
		this.accountViewTypeList = accountViewTypeList;
	}

	/**
	 * @return the accountViewType
	 */
	public WebElement getAccountViewType() {
		return accountViewType;
	}

	/**
	 * @param accountViewType the accountViewType to set
	 */
	public void setAccountViewType(WebElement accountViewType) {
		this.accountViewType = accountViewType;
	}

	/**
	 * @return the saveBtn
	 */
	public WebElement getSaveBtn() {
		return saveBtn;
	}

	/**
	 * @param saveBtn the saveBtn to set
	 */
	public void setSaveBtn(WebElement saveBtn) {
		this.saveBtn = saveBtn;
	}

	/**
	 * @return the searchCode
	 */
	public WebElement getSearchCode() {
		return searchCode;
	}

	/**
	 * @param searchCode the searchCode to set
	 */
	public void setSearchCode(WebElement searchCode) {
		this.searchCode = searchCode;
	}

	/**
	 * @return the recordTodelete
	 */
	public WebElement getRecordTodelete() {
		return recordTodelete;
	}

	/**
	 * @param recordTodelete the recordTodelete to set
	 */
	public void setRecordTodelete(WebElement recordTodelete) {
		this.recordTodelete = recordTodelete;
	}

	/**
	 * @return the deleteBtn
	 */
	public WebElement getDeleteBtn() {
		return deleteBtn;
	}

	/**
	 * @param deleteBtn the deleteBtn to set
	 */
	public void setDeleteBtn(WebElement deleteBtn) {
		this.deleteBtn = deleteBtn;
	}

	/**
	 * @return the confirmBtn
	 */
	public WebElement getConfirmBtn() {
		return confirmBtn;
	}

	/**
	 * @param confirmBtn the confirmBtn to set
	 */
	public void setConfirmBtn(WebElement confirmBtn) {
		this.confirmBtn = confirmBtn;
	}
}
