package com.opencellsoft.testng.pages.setup;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class CreditCategoriesPage extends BasePage {

	public CreditCategoriesPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]")
	private WebElement buttonNew;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div/div[1]/div/input")
	private WebElement codeCreditCat;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div/div[2]/div/input")
	private WebElement descCreditCat;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button/span[1]")
	private WebElement saveBtn;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[5]/td[2]/span")
	private WebElement recordTodelete;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div[2]/button/span[1]/span")
	private WebElement deleteBtn;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div[2]/button[2]/span[1]")
	private WebElement confirmBtn;
	public void gotoListPage(WebDriver driver) {
		WebElement setUpMenu = driver
				.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[9]/span[2]"));
		moveMouse(setUpMenu);

		WebElement CreditCategMenu = driver.findElement(By
				.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/a[10]"));
		moveMouseAndClick(CreditCategMenu);

	}
	public void fillFormCreditCat(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(buttonNew);
		moveMouseAndClick(codeCreditCat);
		codeCreditCat.clear();
		codeCreditCat.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(descCreditCat);
		descCreditCat.clear();
		descCreditCat.sendKeys((String) data.get(Constants.CODE));
		}
	public void saveCreditCat(WebDriver driver) throws InterruptedException {
		moveMouseAndClick(saveBtn);
	}
	public void deleteCreditCat(WebDriver driver, Map<String, String> data) throws InterruptedException {
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
	 * @return the codeCreditCat
	 */
	public WebElement getCodeCreditCat() {
		return codeCreditCat;
	}
	/**
	 * @param codeCreditCat the codeCreditCat to set
	 */
	public void setCodeCreditCat(WebElement codeCreditCat) {
		this.codeCreditCat = codeCreditCat;
	}
	/**
	 * @return the descCreditCat
	 */
	public WebElement getDescCreditCat() {
		return descCreditCat;
	}
	/**
	 * @param descCreditCat the descCreditCat to set
	 */
	public void setDescCreditCat(WebElement descCreditCat) {
		this.descCreditCat = descCreditCat;
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
