package com.opencellsoft.testng.pages.customers;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class BillingAccountPage extends BasePage {
    
    @FindBy(id = "parentTab:formId:childTab:customerSelectId_selectLink")
    private WebElement custAccountSelect;
    
    @FindBy(xpath = "/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement custAccount;
    
    @FindBy(id = "parentTab:formId:childTab:description")
    private WebElement description;
    
    @FindBy(id = "parentTab:formId:childTab:code_txt")
    private WebElement billingAccountCode;
    
    @FindBy(id = "parentTab:formId:childTab:billingCycleSelectId_selectLink")
    private WebElement billingCycleSelect;
    
    @FindBy(xpath = "/html/body/div[10]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement billingCycle;
    
    @FindBy(id = "parentTab:formId:childTab:trCountrySelectId_label")
    private WebElement countrySelect;
    
    @FindBy(id = "parentTab:formId:childTab:trCountrySelectId_1")
    private WebElement country;
    
    @FindBy(id = "parentTab:formId:childTab:trLanguageSelectId_label")
    private WebElement languageSelect;
    
    @FindBy(id = "parentTab:formId:childTab:trLanguageSelectId_1")
    private WebElement language;
    
    @FindBy(id = "parentTab:formId:formButtonsCC:saveButtonAjax")
    private WebElement saveBtn;
    
    @FindBy(id = "searchForm:buttonNew")
    private WebElement buttonNew;
    
    @FindBy(id = "parentTab:formId:formButtonsCC:backButton")
    private WebElement backButton;
    
    @FindBy(id = "searchForm:code_txt")
    private WebElement searchCode;
    
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement buttonSearch;
    
    @FindBy(id = "datatable_results:0:code_id_message_link")
    private WebElement billingAccountToDelete;
    
    public BillingAccountPage(WebDriver driver) {
        super(driver);
    }
    
    public void gotoListPage(WebDriver driver) {
        WebElement customersMenu = driver.findElement(By.id("menu:crm"));
        moveMouse(customersMenu);
        
        WebElement billingAccounts = driver.findElement(By.id("menu:billingAccounts"));
        moveMouseAndClick(billingAccounts);
        
    }
    
    public void fillFormBillingAccount(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(buttonNew);
        moveMouseAndClick(custAccountSelect);
        moveMouseAndClick(custAccount);
        moveMouseAndClick(billingAccountCode);
        billingAccountCode.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(description);
        description.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(billingCycleSelect);
        moveMouseAndClick(billingCycle);
        moveMouseAndClick(countrySelect);
        moveMouseAndClick(country);
        moveMouseAndClick(languageSelect);
        moveMouseAndClick(language);
        moveMouseAndClick(saveBtn);
        moveMouseAndClick(backButton);
        moveMouseAndClick(searchCode);
        searchCode.clear();
        searchCode.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(buttonSearch);
        
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement resultsDeleteLink = driver
                    .findElement(By.id("datatable_results:0:resultsdeletelink"));
                moveMouseAndClick(resultsDeleteLink);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
    }
    
    /**
     * @return the custAccountSelect
     */
    public WebElement getCustAccountSelect() {
        return custAccountSelect;
    }
    
    /**
     * @param custAccountSelect the custAccountSelect to set
     */
    public void setCustAccountSelect(WebElement custAccountSelect) {
        this.custAccountSelect = custAccountSelect;
    }
    
    /**
     * @return the custAccount
     */
    public WebElement getCustAccount() {
        return custAccount;
    }
    
    /**
     * @param custAccount the custAccount to set
     */
    public void setCustAccount(WebElement custAccount) {
        this.custAccount = custAccount;
    }
    
    /**
     * @return the description
     */
    public WebElement getDescription() {
        return description;
    }
    
    /**
     * @param description the description to set
     */
    public void setDescription(WebElement description) {
        this.description = description;
    }
    
    /**
     * @return the billingAccountCode
     */
    public WebElement getBillingAccountCode() {
        return billingAccountCode;
    }
    
    /**
     * @param billingAccountCode the billingAccountCode to set
     */
    public void setBillingAccountCode(WebElement billingAccountCode) {
        this.billingAccountCode = billingAccountCode;
    }
    
    /**
     * @return the billingCycleSelect
     */
    public WebElement getBillingCycleSelect() {
        return billingCycleSelect;
    }
    
    /**
     * @param billingCycleSelect the billingCycleSelect to set
     */
    public void setBillingCycleSelect(WebElement billingCycleSelect) {
        this.billingCycleSelect = billingCycleSelect;
    }
    
    /**
     * @return the billingCycle
     */
    public WebElement getBillingCycle() {
        return billingCycle;
    }
    
    /**
     * @param billingCycle the billingCycle to set
     */
    public void setBillingCycle(WebElement billingCycle) {
        this.billingCycle = billingCycle;
    }
    
    /**
     * @return the countrySelect
     */
    public WebElement getCountrySelect() {
        return countrySelect;
    }
    
    /**
     * @param countrySelect the countrySelect to set
     */
    public void setCountrySelect(WebElement countrySelect) {
        this.countrySelect = countrySelect;
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
     * @return the languageSelect
     */
    public WebElement getLanguageSelect() {
        return languageSelect;
    }
    
    /**
     * @param languageSelect the languageSelect to set
     */
    public void setLanguageSelect(WebElement languageSelect) {
        this.languageSelect = languageSelect;
    }
    
    /**
     * @return the language
     */
    public WebElement getLanguage() {
        return language;
    }
    
    /**
     * @param language the language to set
     */
    public void setLanguage(WebElement language) {
        this.language = language;
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
     * @return the backButton
     */
    public WebElement getBackButton() {
        return backButton;
    }
    
    /**
     * @param backButton the backButton to set
     */
    public void setBackButton(WebElement backButton) {
        this.backButton = backButton;
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
     * @return the buttonSearch
     */
    public WebElement getButtonSearch() {
        return buttonSearch;
    }
    
    /**
     * @param buttonSearch the buttonSearch to set
     */
    public void setButtonSearch(WebElement buttonSearch) {
        this.buttonSearch = buttonSearch;
    }
    
    /**
     * @return the billingAccountToDelete
     */
    public WebElement getBillingAccountToDelete() {
        return billingAccountToDelete;
    }
    
    /**
     * @param billingAccountToDelete the billingAccountToDelete to set
     */
    public void setBillingAccountToDelete(WebElement billingAccountToDelete) {
        this.billingAccountToDelete = billingAccountToDelete;
    }
}
