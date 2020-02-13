package com.opencellsoft.testng.pages.setup;

import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class CalendarsPage extends BasePage {

	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span")
	private WebElement buttonNew;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[1]/div/div/div[1]/div/input")
	private WebElement calendarCode;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[1]/div/div/div[2]/div/input")
	private WebElement calendarDesc;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[1]/div/div/div[3]/div/div/div")
	private WebElement calendarTypeList;
	@FindBy(xpath = "/html/body/div[4]/div[2]/ul/li[4]")
	private WebElement calendarType;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[2]/div/button/span[1]")
	private WebElement saveBtn;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[1]/div[1]/div[1]/div/input")
	private WebElement searchCode;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr/td[2]/span")
	private WebElement recordTodelete;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[2]/div/button[2]/span[1]/span")
	private WebElement deleteBtn;
	@FindBy(xpath = "/html/body/div[4]/div[2]/div[3]/button[2]/span[1]")
	private WebElement confirmBtn;

	public CalendarsPage(WebDriver driver) {
		super(driver);
	}

	public void gotoListPage(WebDriver driver) {
		WebElement setUpMenu = driver
				.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[9]/span[2]"));
		moveMouse(setUpMenu);

		WebElement billingMenu = driver.findElement(By
				.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/div[1]/span[2]"));
		moveMouseAndClick(billingMenu);

		WebElement calendarMenu = driver.findElement(By.xpath(
				"/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/div[2]/div/div/a[2]"));
		moveMouseAndClick(calendarMenu);
	}

	public void fillFormCalendar(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(buttonNew);
		moveMouseAndClick(calendarCode);
		calendarCode.clear();
		calendarCode.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(calendarDesc);
		calendarDesc.clear();
		calendarDesc.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(calendarTypeList);
		moveMouseAndClick(calendarType);
	}

	public void saveCalendar(WebDriver driver) throws InterruptedException {
		moveMouseAndClick(saveBtn);
	}

	public void searchCalendar(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(searchCode);
		searchCode.clear();
		searchCode.sendKeys((String) data.get(Constants.CODE));
	}

	public void deleteCalendar(WebDriver driver, Map<String, String> data) throws InterruptedException {
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
	 * @return the calendarCode
	 */
	public WebElement getCalendarCode() {
		return calendarCode;
	}

	/**
	 * @param calendarCode the calendarCode to set
	 */
	public void setCalendarCode(WebElement calendarCode) {
		this.calendarCode = calendarCode;
	}

	/**
	 * @return the calendarDesc
	 */
	public WebElement getCalendarDesc() {
		return calendarDesc;
	}

	/**
	 * @param calendarDesc the calendarDesc to set
	 */
	public void setCalendarDesc(WebElement calendarDesc) {
		this.calendarDesc = calendarDesc;
	}

	/**
	 * @return the calendarTypeList
	 */
	public WebElement getCalendarTypeList() {
		return calendarTypeList;
	}

	/**
	 * @param calendarTypeList the calendarTypeList to set
	 */
	public void setCalendarTypeList(WebElement calendarTypeList) {
		this.calendarTypeList = calendarTypeList;
	}

	/**
	 * @return the calendarType
	 */
	public WebElement getCalendarType() {
		return calendarType;
	}

	/**
	 * @param calendarType the calendarType to set
	 */
	public void setCalendarType(WebElement calendarType) {
		this.calendarType = calendarType;
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
