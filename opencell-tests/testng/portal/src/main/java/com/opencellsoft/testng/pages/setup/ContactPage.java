package com.opencellsoft.testng.pages.setup;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class ContactPage extends BasePage {
	public ContactPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]")
	private WebElement buttonNew;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[1]/div/div/input")
	private WebElement codeContact;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[2]/div/div/input")
	private WebElement descContact;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[3]/div/div/input")
	private WebElement lastNameContact;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[4]/div/div/input")
	private WebElement fisrtNameContact;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[2]/span[1]/span")
	private WebElement contactInfoTab;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[2]/div/div[1]/div/div/input")
	private WebElement emailContact;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[3]/span[1]/span/span")
	private WebElement adressTab;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div[1]/div[1]/div/input")
	private WebElement adress1;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div[2]/div[1]/div/input")
	private WebElement zipCode;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div[2]/div[2]/div/input")
	private WebElement city;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div[2]/div[3]/div/input")
	private WebElement country;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button/span[1]")
	private WebElement saveBtn;
	@FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[2]/td[2]/span")
	private WebElement recordTodelete;
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div[2]/button/span[1]/span")
	private WebElement deleteBtn;
	@FindBy(xpath = "/html/body/div[3]/div[2]/div[2]/button[2]/span[1]")
	private WebElement confirmBtn;

	public void gotoListPage(WebDriver driver) {
		WebElement setUpMenu = driver
				.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[9]/span[2]"));
		moveMouse(setUpMenu);

		WebElement billingMenu = driver.findElement(By
				.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/div[1]/span[2]"));
		moveMouseAndClick(billingMenu);

		WebElement contactMenu = driver.findElement(
				By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/a[8]"));
		moveMouseAndClick(contactMenu);
	}

	public void fillFormContact(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(buttonNew);
		moveMouseAndClick(codeContact);
		codeContact.clear();
		codeContact.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(descContact);
		descContact.clear();
		descContact.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(fisrtNameContact);
		fisrtNameContact.clear();
		fisrtNameContact.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(contactInfoTab);
		moveMouseAndClick(emailContact);
		emailContact.clear();
		emailContact.sendKeys("test@gmail.com");
		moveMouseAndClick(adressTab);
		moveMouseAndClick(adress1);
		adress1.clear();
		adress1.sendKeys("adress");
		moveMouseAndClick(zipCode);
		zipCode.clear();
		zipCode.sendKeys("zipCode");
		moveMouseAndClick(city);
		city.clear();
		city.sendKeys("city");
		moveMouseAndClick(country);
		country.clear();
		country.sendKeys("country");

	}

	public void saveContact(WebDriver driver) throws InterruptedException {
		moveMouseAndClick(saveBtn);
	}

	public void deleteContact(WebDriver driver, Map<String, String> data) throws InterruptedException {
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
	 * @return the codeContact
	 */
	public WebElement getCodeContact() {
		return codeContact;
	}

	/**
	 * @param codeContact the codeContact to set
	 */
	public void setCodeContact(WebElement codeContact) {
		this.codeContact = codeContact;
	}

	/**
	 * @return the descContact
	 */
	public WebElement getDescContact() {
		return descContact;
	}

	/**
	 * @param descContact the descContact to set
	 */
	public void setDescContact(WebElement descContact) {
		this.descContact = descContact;
	}

	/**
	 * @return the lastNameContact
	 */
	public WebElement getLastNameContact() {
		return lastNameContact;
	}

	/**
	 * @param lastNameContact the lastNameContact to set
	 */
	public void setLastNameContact(WebElement lastNameContact) {
		this.lastNameContact = lastNameContact;
	}

	/**
	 * @return the fisrtNameContact
	 */
	public WebElement getFisrtNameContact() {
		return fisrtNameContact;
	}

	/**
	 * @param fisrtNameContact the fisrtNameContact to set
	 */
	public void setFisrtNameContact(WebElement fisrtNameContact) {
		this.fisrtNameContact = fisrtNameContact;
	}

	/**
	 * @return the contactInfoTab
	 */
	public WebElement getContactInfoTab() {
		return contactInfoTab;
	}

	/**
	 * @param contactInfoTab the contactInfoTab to set
	 */
	public void setContactInfoTab(WebElement contactInfoTab) {
		this.contactInfoTab = contactInfoTab;
	}

	/**
	 * @return the emailContact
	 */
	public WebElement getEmailContact() {
		return emailContact;
	}

	/**
	 * @param emailContact the emailContact to set
	 */
	public void setEmailContact(WebElement emailContact) {
		this.emailContact = emailContact;
	}

	/**
	 * @return the adressTab
	 */
	public WebElement getAdressTab() {
		return adressTab;
	}

	/**
	 * @param adressTab the adressTab to set
	 */
	public void setAdressTab(WebElement adressTab) {
		this.adressTab = adressTab;
	}

	/**
	 * @return the adress1
	 */
	public WebElement getAdress1() {
		return adress1;
	}

	/**
	 * @param adress1 the adress1 to set
	 */
	public void setAdress1(WebElement adress1) {
		this.adress1 = adress1;
	}

	/**
	 * @return the zipCode
	 */
	public WebElement getZipCode() {
		return zipCode;
	}

	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(WebElement zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * @return the city
	 */
	public WebElement getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(WebElement city) {
		this.city = city;
	}

	/**
	 * @return the country
	 */
	public WebElement getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(WebElement country) {
		this.country = country;
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
