package com.opencellsoft.testng.pages.setup;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class ServiceTypePage  extends BasePage{
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span")
	private WebElement buttonNew;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[1]/div/div[1]/div/div/input")
	private WebElement codeServiceType;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[1]/div/div[2]/div/div/input")
	private WebElement descServiceType;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[2]/div/button/span[1]")
	private WebElement saveBtn;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[1]/div[1]/div[1]/div/input")
	private WebElement searchCode;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[1]/td[2]/span")
	private WebElement recordTodelete;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[2]/div/button[2]/span[1]/span")
	private WebElement deleteBtn;
	@FindBy(xpath = "/html/body/div[4]/div[2]/div[3]/button[2]/span[1]")
	private WebElement confirmBtn;

	public ServiceTypePage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
	public void gotoListPage(WebDriver driver) {
		WebElement setUpMenu = driver
				.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[9]/span[2]"));
		moveMouse(setUpMenu);

		WebElement billingMenu = driver.findElement(By
				.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/div[1]/span[2]"));
		moveMouseAndClick(billingMenu);

		WebElement invServiceTypeMenu = driver.findElement(By.xpath(
				"/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/div[2]/div/div/a[8]"));
		moveMouseAndClick(invServiceTypeMenu);
	}
	public void fillFormServiceType(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(buttonNew);
		moveMouseAndClick(codeServiceType);
		codeServiceType.clear();
		codeServiceType.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(descServiceType);
		descServiceType.clear();
		descServiceType.sendKeys("11");
	}

	public void saveInvSeq(WebDriver driver) throws InterruptedException {
		moveMouseAndClick(saveBtn);
	}
	public void searchInvSeq(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(searchCode);
		searchCode.clear();
		searchCode.sendKeys((String) data.get(Constants.CODE));
	}

	public void deleteInvSeq(WebDriver driver, Map<String, String> data) throws InterruptedException {
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
	 * @return the codeServiceType
	 */
	public WebElement getCodeServiceType() {
		return codeServiceType;
	}
	/**
	 * @param codeServiceType the codeServiceType to set
	 */
	public void setCodeServiceType(WebElement codeServiceType) {
		this.codeServiceType = codeServiceType;
	}
	/**
	 * @return the descServiceType
	 */
	public WebElement getDescServiceType() {
		return descServiceType;
	}
	/**
	 * @param descServiceType the descServiceType to set
	 */
	public void setDescServiceType(WebElement descServiceType) {
		this.descServiceType = descServiceType;
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
