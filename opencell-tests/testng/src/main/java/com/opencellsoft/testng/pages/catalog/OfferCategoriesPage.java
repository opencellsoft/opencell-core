package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class OfferCategoriesPage extends BasePage {
	/**
	 * code.
	 */
	@FindBy(id = "formId:tabView:code_txt")
	private WebElement code;
	/**
	 * name.
	 */
	@FindBy(id = "formId:tabView:name_txt")
	private WebElement name;

	/**
	 * saveBtn.
	 */
	@FindBy(id = "formId:formButtonsCC:saveButtonAjax")
	private WebElement saveBtn;

	/**
	 * deleteOfferTemplateCategory.
	 */
	@FindBy(id = "deleteOfferTemplateCategory")
	private WebElement deleteOfferTemplateCategory;

	public OfferCategoriesPage(WebDriver driver) {
		super(driver);
	}

	public void gotoListPage(WebDriver driver) {
		WebElement catalogMenu = driver.findElement(By.id("menu:catalog"));
		moveMouse(catalogMenu);

		WebElement offers = driver.findElement(By.id("menu:offerManagement"));
		moveMouse(offers);

		WebElement offersCat = driver.findElement(By.id("menu:offerTemplateCategories"));
		moveMouseAndClick(offersCat);
	}

	public void fillData(WebDriver driver, Map<String, String> data) throws InterruptedException {
		code.click();
		code.clear();
		code.sendKeys((String) data.get(Constants.CODE));
		name.click();
		name.clear();
		name.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(saveBtn);
		moveMouseAndClick(deleteOfferTemplateCategory);
		WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
		yes.click();
	}
}
