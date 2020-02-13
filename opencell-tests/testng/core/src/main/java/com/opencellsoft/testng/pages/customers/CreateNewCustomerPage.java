
package com.opencellsoft.testng.pages.customers;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * @author Miftah
 *
 */
public class CreateNewCustomerPage extends BasePage {
    /**
     * loader popup.
     */
    @FindBy(css = ".modal-content")
    private WebElement loaderModal;
    
    /**
     * customer menu.
     */
    @FindBy(css = ".nav .fa-users")
    private WebElement customerMenu;
    
    /**
     * customer page.
     */
    @FindBy(id = "menu:customers")
    private WebElement customerPage;
    
    /**
     * customer description field.
     */
    @FindBy(id = "formCustomer:tabView:description")
    private WebElement descriptionIpt;
    
    /**
     * customer's code field.
     */
    @FindBy(id = "formCustomer:tabView:code")
    private WebElement code;
    
    /**
     * customer category drop down list .
     */
    @FindBy(id = "formCustomer:tabView:customerCategory_entity_label")
    private WebElement customerCategorylist;
    
    /**
     * add customer category .
     */
    @FindBy(id = "formCustomer:tabView:customerCategory_entity_1")
    private WebElement customerCategory;
    
    /**
     * save button.
     */
    @FindBy(id = "formCustomer:formButtonsCC:saveButton")
    private WebElement saveBtn;
    
    /**
     * Button Delete Customer.
     */
    @FindBy(id = "deleteAnCustomerHierarchy")
    private WebElement deleteCustomerBtn;
    
    /**
     * modalForm to confirm Delete an Customer.
     */
    @FindBy(id = "confirmDeleteCustomer")
    private WebElement confirmDeleteCustomer;
    
    /***************************************************************************************
     * 
     * @param driver web driver.
     */
    public CreateNewCustomerPage(final WebDriver driver) {
        super(driver);
    }
    
    /**
     * @return instance of CreateNewCustomerPage
     */
    public void openCustomersList(WebDriver driver) {
        WebElement customersMenu = driver.findElement(By.id("menu:crm"));
        moveMouseAndClick(customersMenu);
        WebElement customersPage = driver.findElement(By.id("menu:customers"));
        moveMouseAndClick(customersPage);
    }
    
    /**
     * @return the loaderModal
     */
    public WebElement getLoaderModal() {
        return loaderModal;
    }
    
    /**
     * @param loaderModal the loaderModal to set
     */
    public void setLoaderModal(WebElement loaderModal) {
        this.loaderModal = loaderModal;
    }
    
    /**
     * @return the customerPage
     */
    public WebElement getCustomerPage() {
        return customerPage;
    }
    
    /**
     * @param customerPage the customerPage to set
     */
    public void setCustomerPage(WebElement customerPage) {
        this.customerPage = customerPage;
    }
    
    /**
     * method to force filling fields .
     * 
     * @param driver
     * @param element
     * @param keysToSend
     */
    public void forceSendkeys(WebDriver driver, WebElement element, String keysToSend) {
        Actions action = new Actions(driver);
        action.moveToElement(element).click().sendKeys(keysToSend).perform();
    }
    
    /***********************************************************************************
     * fill customer.
     * 
     * @param data map containing the infos to fill the page.
     * @return instance of NewCustomerPage
     * @throws InterruptedException
     */
    public void fillCrmHierarchy(WebDriver driver, Map<String, String> data) throws InterruptedException {
        WebElement addNewCustomerMenu = driver
                .findElement(By.id("searchForm:buttonNew"));
            moveMouseAndClick(addNewCustomerMenu);
        descriptionIpt.click();
        descriptionIpt.clear();
        descriptionIpt.sendKeys((String) data.get(Constants.DESCRIPTION));
        
        code.click();
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        
        moveMouseAndClick(customerCategorylist);
        WebElement customerCategory = driver
            .findElement(By.id("formCustomer:tabView:customerCategory_entity_1"));
        moveMouseAndClick(customerCategory);
        
        WebElement saveBtn = driver.findElement(By.id("formCustomer:formButtonsCC:saveButton"));
        moveMouseAndClick(saveBtn);
        
        WebElement backBtn = driver.findElement(By.id("formCustomer:formButtonsCC:backButton"));
        moveMouseAndClick(backBtn);
        
        
        WebElement codeCustSearch = driver.findElement(By.id("searchForm:code_txt"));
        moveMouseAndClick(codeCustSearch);
        codeCustSearch.clear();
        codeCustSearch.sendKeys((String) data.get(Constants.CODE));
        WebElement btnSearch = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(btnSearch); 
        
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement rowTODelete = driver
                    .findElement(By.id("datatable_results:0:code_id_message_link"));
                moveMouseAndClick(rowTODelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        WebElement deletebttn = driver.findElement(By.id("formCustomer:formButtonsCC:deletelink"));
        deletebttn.click();

        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
        
        /*
        
        moveMouseAndClick(driver.findElement(By.linkText("Add customer account")));
        
       WebElement customerAccountCode = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:code_txt"));
        waitUntilElementDisplayed(customerAccountCode, driver);
        customerAccountCode.click();
        customerAccountCode.clear();
        customerAccountCode.sendKeys((String) data.get(Constants.CODE));
        WebElement customerAccountCurrency = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:currencySelectId_label"));
        moveMouseAndClick(customerAccountCurrency);
        WebElement currency = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:currencySelectId_1"));
        moveMouseAndClick(currency);
        WebElement customerAccountLanguage = driver
            .findElement(By.id("arentTab:formCustomerAccount:childTab:trLanguageSelectId_label"));
        moveMouseAndClick(customerAccountLanguage);
        WebElement language = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:trLanguageSelectId_1"));
        moveMouseAndClick(language);
        WebElement infoTab = driver.findElement(By.xpath(
            "/html/body/div[2]/span/span[2]/div/div/div/form/div/div[2]/div/div[2]/div/ul/li[2]/a"));
        moveMouseAndClick(infoTab);
        WebElement payMethod = driver.findElements(By.className("ui-button-text-only")).get(0);
        moveMouseAndClick(payMethod);
        
        WebElement aliaCode = driver
            .findElement(By.id("parentTab:formCustomerAccount:childTab:alias_txt"));
        aliaCode.click();
        aliaCode.sendKeys("1250");
        
        WebElement addAlia = driver.findElements(By.className("ui-button-text-only")).get(1);
        forceClick(addAlia);
        
        WebElement saveCustomerAccount = driver
            .findElement(By.id("parentTab:formCustomerAccount:formButtonsCC:saveButton"));
        moveMouseAndClick(saveCustomerAccount);
        
        WebElement billingAccountPage = driver.findElements(By.className("ui-button-text-only"))
            .get(4);
        moveMouseAndClick(billingAccountPage);
        
        WebElement billingAccountCode = driver
            .findElement(By.id("parentTab:formId:childTab:code_txt"));
        billingAccountCode.click();
        billingAccountCode.sendKeys((String) data.get(Constants.CODE));
        
        WebElement billingCycleSelectLink = driver
            .findElement(By.id("parentTab:formId:childTab:billingCycleSelectId_selectLink"));
        moveMouseAndClick(billingCycleSelectLink);
        
        WebElement billingCycle = driver.findElement(
            By.xpath("/html/body/div[10]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]"));
        moveMouseAndClick(billingCycle);
        
        WebElement billingAccountCountryList = driver
            .findElement(By.id("parentTab:formId:childTab:trCountrySelectId_label"));        
        moveMouseAndClick(billingAccountCountryList);
        WebElement billingAccountCountry = driver
            .findElement(By.id("parentTab:formId:childTab:trCountrySelectId_1"));        
        moveMouseAndClick(billingAccountCountry);
        
        WebElement SaveBillingAccount = driver
            .findElement(By.id("parentTab:formId:formButtonsCC:saveButtonAjax"));        
        moveMouseAndClick(SaveBillingAccount);
        
        WebElement userAccountPage = driver.findElements(By.className("ui-button-text-only"))
            .get(6);
        moveMouseAndClick(userAccountPage);
        WebElement userAccountCode = driver
            .findElement(By.id("uparentTab:userAccountFormId:userAccountTab:code_txt"));
        userAccountCode.click();
        userAccountCode.sendKeys((String) data.get(Constants.CODE));
        
        WebElement userAccountSave = driver
            .findElement(By.id("parentTab:userAccountFormId:formButtonsCC:saveButtonAjax"));        
        moveMouseAndClick(userAccountSave);
        WebElement subscriptionPage = driver.findElements(By.className("ui-button-text-only"))
            .get(4);
        moveMouseAndClick(subscriptionPage);
        WebElement subscriptionOfferList = driver.findElement(
            By.id("subscriptionTab:subscriptionFormId:informationTab:offerSelectId_selectLink"));
                
        moveMouseAndClick(subscriptionOfferList);
        
        WebElement subscriptionOffer = driver.findElement(
            By.xpath("/html/body/div[11]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]"));
        
        waitUntilElementDisplayed(subscriptionOffer, driver);
        moveMouseAndClick(subscriptionOffer);
        
        WebElement subscriptionCode = driver
            .findElement(By.id("subscriptionTab:subscriptionFormId:informationTab:code_txt"));
        subscriptionCode.click();
        subscriptionCode.sendKeys((String) data.get(Constants.CODE));
        
        WebElement subscriptionSellerList = driver.findElement(
            By.id("subscriptionTab:subscriptionFormId:informationTab:sellerSelectId_selectLink"));
        moveMouseAndClick(subscriptionSellerList);
        
        WebElement subscriptionSeller = driver.findElement(
            By.xpath("/html/body/div[14]/div[2]/form/div[2]/div[2]/table/tbody/tr[2]/td[1]"));
        
        waitUntilElementDisplayed(subscriptionSeller, driver);
        moveMouseAndClick(subscriptionSeller);
        
        WebElement endAgreementDate = driver.findElement(
            By.id("subscriptionTab:subscriptionFormId:informationTab:endAgreementDate_date_input"));
        
        waitUntilElementDisplayed(endAgreementDate, driver);
        endAgreementDate.click();
        endAgreementDate.sendKeys("08/08/2019");
        
        WebElement saveSub = driver
            .findElement(By.id("subscriptionTab:subscriptionFormId:formButtonsCC:saveButtonAjax"));
        
        moveMouseAndClick(saveSub);
        WebElement serviceTab = driver
            .findElement(By.xpath("/html/body/div[2]/span/span[2]/div/ul/li[1]/a"));
        moveMouseAndClick(serviceTab);
        
        WebElement serviceToInstantiate = driver.findElement(By.xpath(
            "/html/body/div[2]/span/span[2]/div/div/div[1]/form[3]/div/div/div[1]/table/tbody/tr[1]/td[1]/div/div[2]/span"));
        
        moveMouseAndClick(serviceToInstantiate);
        
        WebElement instantiate = driver.findElement(By.id("subscriptionTab:cbutton"));
        
        moveMouseAndClick(instantiate);
        
        WebElement serviceToActivate = driver.findElement(By.xpath(
            "html/body/div[2]/span/span[2]/div/div/div[1]/form[2]/div/div[1]/table/tbody/tr/td[1]/div/div[2]/span"));
        
        moveMouseAndClick(serviceToActivate);
        
        WebElement activate = driver.findElements(By.className("ui-button-text-only")).get(0);
        moveMouseAndClick(activate);
        
        WebElement confirmYes = driver.findElements(By.className("ui-confirmdialog-yes")).get(0);
        moveMouseAndClick(confirmYes);
        
        
         * public void generateInvoice(WebDriver driver, Map<String, String> data) throws
         * InterruptedException {
         
        
        WebElement billingAccountMenu = driver.findElement(
            By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[3]/a/span[1]"));
        moveMouseAndClick(billingAccountMenu);
        
        WebElement billingAccountList = driver.findElement(
            By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[3]/ul/li[4]/a/span"));
        moveMouseAndClick(billingAccountList);
        
        WebElement bACodeToSearch = driver.findElement(By.id("searchForm:code_txt"));
        moveMouseAndClick(bACodeToSearch);
        bACodeToSearch.sendKeys((String) data.get(Constants.CODE));
        
        WebElement buttonSearch = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(buttonSearch);
        
        WebElement bAToSelect = driver
            .findElement(By.id("datatable_results:0:code_id_message_link"));
        moveMouseAndClick(bAToSelect);
        
        WebElement generateInvoice = driver.findElements(By.className("ui-button-text-only"))
            .get(7);
        moveMouseAndClick(generateInvoice);
        
    }*/
    }
    /**
     * @return the code
     */
    public WebElement getCode() {
        return code;
    }
    
    /**
     * @param code the code to set
     */
    public void setCode(WebElement code) {
        this.code = code;
    }
    
    /**
     * @return the customerCategorylist
     */
    public WebElement getCustomerCategorylist() {
        return customerCategorylist;
    }
    
    /**
     * @param customerCategorylist the customerCategorylist to set
     */
    public void setCustomerCategorylist(WebElement customerCategorylist) {
        this.customerCategorylist = customerCategorylist;
    }
    
    /**
     * @return the customerCategory
     */
    public WebElement getCustomerCategory() {
        return customerCategory;
    }
    
    /**
     * @param customerCategory the customerCategory to set
     */
    public void setCustomerCategory(WebElement customerCategory) {
        this.customerCategory = customerCategory;
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
     * @return the deleteCustomerBtn
     */
    public WebElement getDeleteCustomerBtn() {
        return deleteCustomerBtn;
    }
    
    /**
     * @param deleteCustomerBtn the deleteCustomerBtn to set
     */
    public void setDeleteCustomerBtn(WebElement deleteCustomerBtn) {
        this.deleteCustomerBtn = deleteCustomerBtn;
    }
    
    /**
     * @return the confirmDeleteCustomer
     */
    public WebElement getConfirmDeleteCustomer() {
        return confirmDeleteCustomer;
    }
    
    /**
     * @param confirmDeleteCustomer the confirmDeleteCustomer to set
     */
    public void setConfirmDeleteCustomer(WebElement confirmDeleteCustomer) {
        this.confirmDeleteCustomer = confirmDeleteCustomer;
    }
    
    /**
     * @return instance of saveCustomer.
     */
    public CreateNewCustomerPage saveCustomer() {
        saveBtn.click();
        return PageFactory.initElements(this.getDriver(), CreateNewCustomerPage.class);
    }
    
    /**
     * @return instance of deleteCustomer.
     */
    public CreateNewCustomerPage deleteCustomer() {
        // deleteCustomerBtn.click();
        forceClick(deleteCustomerBtn);
        return PageFactory.initElements(this.getDriver(), CreateNewCustomerPage.class);
    }
    
    /**
     * @return instance of deleteCustomer.
     */
    public CreateNewCustomerPage confirmDeleteCustomer() {
        forceClick(confirmDeleteCustomer);
        return PageFactory.initElements(this.getDriver(), CreateNewCustomerPage.class);
    }
    
    /**
     * @return descriptionIpt
     */
    public WebElement getDescriptionIpt() {
        return descriptionIpt;
    }
    
    /**
     * @param descriptionIpt the descriptionIpt to set.
     */
    public void setDescriptionIpt(WebElement descriptionIpt) {
        this.descriptionIpt = descriptionIpt;
    }
    
    /**
     * @return the customerMenu
     */
    public WebElement getCustomerMenu() {
        return customerMenu;
    }
    
    /**
     * @param customerMenu the customerMenu to set
     */
    public void setCustomerMenu(WebElement customerMenu) {
        this.customerMenu = customerMenu;
    }
    
}
