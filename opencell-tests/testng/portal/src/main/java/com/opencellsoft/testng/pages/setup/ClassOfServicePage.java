package com.opencellsoft.testng.pages.setup;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class ClassOfServicePage extends BasePage {
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div/a/span[1]/span")
	private WebElement buttonNew;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div[1]/div/div/input")
	private WebElement codeClassService;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div[2]/div/div/input")
	private WebElement descClassService;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/div/button/span[1]")
	private WebElement saveBtn;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[3]/td[2]/span")
	private WebElement recordTodelete;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[2]/div/button[2]/span[1]/span")
	private WebElement deleteBtn;
	@FindBy(xpath = "/html/body/div[4]/div[2]/div[3]/button[2]/span[1]")
	private WebElement confirmBtn;

	public ClassOfServicePage(WebDriver driver) {
		super(driver);
	}
	public void gotoListPage(WebDriver driver) {
		WebElement setUpMenu = driver
				.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[9]/span[2]"));
		moveMouse(setUpMenu);

		WebElement billingMenu = driver.findElement(By
				.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/div[1]/span[2]"));
		moveMouseAndClick(billingMenu);

		WebElement classServiceMenu = driver.findElement(By.xpath(
				"/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/a[7]"));
		moveMouseAndClick(classServiceMenu);
	}
	public void fillFormClassService(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(buttonNew);
		moveMouseAndClick(codeClassService);
		codeClassService.clear();
		codeClassService.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(descClassService);
		descClassService.clear();
		descClassService.sendKeys((String) data.get(Constants.CODE));
	}
	public void saveClassService(WebDriver driver) throws InterruptedException {
		moveMouseAndClick(saveBtn);
	}
	public void deleteClassService(WebDriver driver, Map<String, String> data) throws InterruptedException {
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
	 * @return the codeClassService
	 */
	public WebElement getCodeClassService() {
		return codeClassService;
	}
	/**
	 * @param codeClassService the codeClassService to set
	 */
	public void setCodeClassService(WebElement codeClassService) {
		this.codeClassService = codeClassService;
	}
	/**
	 * @return the descClassService
	 */
	public WebElement getDescClassService() {
		return descClassService;
	}
	/**
	 * @param descClassService the descClassService to set
	 */
	public void setDescClassService(WebElement descClassService) {
		this.descClassService = descClassService;
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
