package com.opencellsoft.testng.pages.payments;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class PaymentGatewaysPage extends BasePage{
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/a/span[1]/span")
	private WebElement buttonNew;

	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[1]/div[1]/div/input")
	private WebElement gatewayCode;

	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[2]/div/div/div/div")
	private WebElement paymentMethodList;

	@FindBy(xpath = "/html/body/div[3]/div[2]/ul/li[2]")
	private WebElement paymentMethod;

	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[5]/div/div/div/div")
	private WebElement sellerCodeList;
	
	@FindBy(xpath = "/html/body/div[3]/div[2]/ul/li[3]")
	private WebElement sellerCode;
	
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[2]")
	private WebElement marchandAccountTab;
	
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[1]/div[3]/div/div/div")
	private WebElement typeList;
	
	@FindBy(xpath = "/html/body/div[3]/div[2]/ul/li[1]")
	private WebElement type;
	
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button")
	private WebElement saveBtn;
	
	@FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[1]/div[1]/div[1]/div/input")
	private WebElement searchCode;
	
	@FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[1]/td[2]/span")
	private WebElement recordToDelete;
	
	@FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div[2]/button/span[1]/span")
	private WebElement deleteBtn;
	
	@FindBy(xpath = "/html/body/div[3]/div[2]/div[2]/button[2]")
	private WebElement confirmBtn;

	
	
	public PaymentGatewaysPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
	public void gotoListPage(WebDriver driver) {
		WebElement paymentsMenu = driver
				.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[7]/span[2]"));
		moveMouse(paymentsMenu);

		WebElement paymentGatewayMenu = driver
				.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[8]/div/div/a[1]"));
		moveMouseAndClick(paymentGatewayMenu);

	}
	public void fillFormGateway(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(buttonNew);
		moveMouseAndClick(gatewayCode);
		gatewayCode.clear();
		gatewayCode.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(paymentMethodList);
		moveMouseAndClick(paymentMethod);
		moveMouseAndClick(sellerCodeList);
		moveMouseAndClick(sellerCode);
		moveMouseAndClick(typeList);
		moveMouseAndClick(type);
	}

	public void saveGateway(WebDriver driver) throws InterruptedException {
		moveMouseAndClick(saveBtn);
	}

	public void searchAndDeleteGateway(WebDriver driver, Map<String, String> data) throws InterruptedException {
		moveMouseAndClick(searchCode);
		searchCode.clear();
		searchCode.sendKeys((String) data.get(Constants.CODE));
		moveMouseAndClick(recordToDelete);
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
	 * @return the gatewayCode
	 */
	public WebElement getGatewayCode() {
		return gatewayCode;
	}
	/**
	 * @param gatewayCode the gatewayCode to set
	 */
	public void setGatewayCode(WebElement gatewayCode) {
		this.gatewayCode = gatewayCode;
	}
	/**
	 * @return the paymentMethodList
	 */
	public WebElement getPaymentMethodList() {
		return paymentMethodList;
	}
	/**
	 * @param paymentMethodList the paymentMethodList to set
	 */
	public void setPaymentMethodList(WebElement paymentMethodList) {
		this.paymentMethodList = paymentMethodList;
	}
	/**
	 * @return the paymentMethod
	 */
	public WebElement getPaymentMethod() {
		return paymentMethod;
	}
	/**
	 * @param paymentMethod the paymentMethod to set
	 */
	public void setPaymentMethod(WebElement paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	/**
	 * @return the sellerCodeList
	 */
	public WebElement getSellerCodeList() {
		return sellerCodeList;
	}
	/**
	 * @param sellerCodeList the sellerCodeList to set
	 */
	public void setSellerCodeList(WebElement sellerCodeList) {
		this.sellerCodeList = sellerCodeList;
	}
	/**
	 * @return the sellerCode
	 */
	public WebElement getSellerCode() {
		return sellerCode;
	}
	/**
	 * @param sellerCode the sellerCode to set
	 */
	public void setSellerCode(WebElement sellerCode) {
		this.sellerCode = sellerCode;
	}
	/**
	 * @return the marchandAccountTab
	 */
	public WebElement getMarchandAccountTab() {
		return marchandAccountTab;
	}
	/**
	 * @param marchandAccountTab the marchandAccountTab to set
	 */
	public void setMarchandAccountTab(WebElement marchandAccountTab) {
		this.marchandAccountTab = marchandAccountTab;
	}
	/**
	 * @return the typeList
	 */
	public WebElement getTypeList() {
		return typeList;
	}
	/**
	 * @param typeList the typeList to set
	 */
	public void setTypeList(WebElement typeList) {
		this.typeList = typeList;
	}
	/**
	 * @return the type
	 */
	public WebElement getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(WebElement type) {
		this.type = type;
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
	 * @return the recordToDelete
	 */
	public WebElement getRecordToDelete() {
		return recordToDelete;
	}
	/**
	 * @param recordToDelete the recordToDelete to set
	 */
	public void setRecordToDelete(WebElement recordToDelete) {
		this.recordToDelete = recordToDelete;
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
