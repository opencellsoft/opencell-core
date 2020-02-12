package com.opencellsoft.testng.pages.setup;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class ChannelsPage extends BasePage{
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span")
	private WebElement buttonNew;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div/div[1]/div/input")
	private WebElement codeChannel;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div/div[2]/div/input")
	private WebElement descChannel;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button/span[1]")
	private WebElement saveBtn;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[1]/div[1]/div[1]/div/input")
	private WebElement searchCode;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[1]/td[2]/span")
	private WebElement recordTodelete;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div[2]/button/span[1]")
	private WebElement deleteBtn;
	@FindBy(xpath = "//html/body/div[3]/div[2]/div[2]/button[2]/span[1]")
	private WebElement confirmBtn;
	public ChannelsPage(WebDriver driver) {
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

		WebElement channelsMenu = driver.findElement(By.xpath(
				"/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/a[6]"));
		moveMouseAndClick(channelsMenu);
	}
	public void fillFormChannel(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(buttonNew);
		moveMouseAndClick(codeChannel);
		codeChannel.clear();
		codeChannel.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(descChannel);
		descChannel.clear();
		descChannel.sendKeys((String) data.get(Constants.CODE));
	}

	public void saveChannel(WebDriver driver) throws InterruptedException {
		moveMouseAndClick(saveBtn);
	}
	public void searchChannel(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(searchCode);
		searchCode.clear();
		searchCode.sendKeys((String) data.get(Constants.CODE));
	}

	public void deleteChannel(WebDriver driver, Map<String, String> data) throws InterruptedException {
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
	 * @return the codeChannel
	 */
	public WebElement getCodeChannel() {
		return codeChannel;
	}
	/**
	 * @param codeChannel the codeChannel to set
	 */
	public void setCodeChannel(WebElement codeChannel) {
		this.codeChannel = codeChannel;
	}
	/**
	 * @return the descChannel
	 */
	public WebElement getDescChannel() {
		return descChannel;
	}
	/**
	 * @param descChannel the descChannel to set
	 */
	public void setDescChannel(WebElement descChannel) {
		this.descChannel = descChannel;
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
