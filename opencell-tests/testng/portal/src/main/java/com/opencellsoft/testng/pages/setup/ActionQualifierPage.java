package com.opencellsoft.testng.pages.setup;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class ActionQualifierPage extends BasePage {
	@FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div/a/span[1]")
	private WebElement buttonNew;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[1]/div/div[1]/div/div/input")
	private WebElement actionQualifierCode;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[1]/div/div[2]/div/div/input")
	private WebElement actionQualifierDesc;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[2]/div/button/span[1]")
	private WebElement saveBtn;
	public ActionQualifierPage(WebDriver driver) {
		super(driver);
	}
	public void gotoListPage(WebDriver driver) {
		WebElement setUpMenu = driver
				.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[9]/span[2]"));
		moveMouse(setUpMenu);

		WebElement actionMenu = driver
				.findElement(By.xpath("/html/body/div/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/a[4]"));
		moveMouseAndClick(actionMenu);

	}
	public void fillFormAction(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(buttonNew);
		moveMouseAndClick(actionQualifierCode);
		actionQualifierCode.clear();
		actionQualifierCode.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(actionQualifierDesc);
		actionQualifierDesc.clear();
		actionQualifierDesc.sendKeys((String) data.get(Constants.CODE));
	}
	public void saveAction(WebDriver driver) throws InterruptedException {
		moveMouseAndClick(saveBtn);
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
	 * @return the actionQualifierCode
	 */
	public WebElement getActionQualifierCode() {
		return actionQualifierCode;
	}
	/**
	 * @param actionQualifierCode the actionQualifierCode to set
	 */
	public void setActionQualifierCode(WebElement actionQualifierCode) {
		this.actionQualifierCode = actionQualifierCode;
	}
	/**
	 * @return the actionQualifierDesc
	 */
	public WebElement getActionQualifierDesc() {
		return actionQualifierDesc;
	}
	/**
	 * @param actionQualifierDesc the actionQualifierDesc to set
	 */
	public void setActionQualifierDesc(WebElement actionQualifierDesc) {
		this.actionQualifierDesc = actionQualifierDesc;
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
}
