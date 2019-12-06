package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author MIFTAH
 *
 */
public class CustomersAccountingOperationsTypesPage extends BasePage {
    
    /**
     * code.
     */
    @FindBy(id = "formId:code_txt")
    private WebElement codeCtp;
    
    /**
     * account Code Client Side.
     */
    @FindBy(id = "formId:accountCodeClientSide_txt")
    private WebElement accCode;
    
    /**
     * description.
     */
    @FindBy(id = "formId:description_txt")
    private WebElement descriptionCtp;
    
    /**
     * save button.
     */
    @FindBy(id = "formId:formButtonsCC:saveButton")
    private WebElement saveBttnCtp;
    
    /**
     * button search.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBttnCtp;
    
    /**
     * search code.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeSearch;
    
    /**
     * button delete.
     */
    @FindBy(id = "formId:formButtonsCC:deletelink")
    private WebElement deleteBttn;
    
    /**
     * @param driver WebDriver
     */
    public CustomersAccountingOperationsTypesPage(final WebDriver driver) {
        super(driver);
        
    }
    
    /**
     * click on configuration -> customers -> Account Operations Types.
     * 
     * @param driver instance of WebDriver
     * @throws InterruptedException
     */
    public void gotoListPage(WebDriver driver) throws InterruptedException {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);
        
        WebElement customersMenu = driver.findElement(By.id("menu:menu"));
        moveMouse(customersMenu);
        
        WebElement titelsMenu = driver.findElement(By.id("menu:titles"));
        moveMouse(titelsMenu);
        
        WebElement occSubMenu = driver.findElement(By.id("menu:occTemplates"));
        moveMouseAndClick(occSubMenu);
    }
    
    /**
     * Go to New Account Operations Types Creation page.
     * 
     * @param driver instance of WebDriver
     */
    public void gotoNewPage(WebDriver driver) {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
    }
    
    /**
     * the button save.
     * 
     * @param driver WebDriver
     */
    
    public void saveOperation(WebDriver driver) {
        WebElement btnSave = driver.findElement(By.id("formId:formButtonsCC:saveButton"));
        moveMouseAndClick(btnSave);
    }
    
    /**
     * fill the new account Operation Type.
     * 
     * @param driver WebDriver
     * @param data Map
     * @throws InterruptedException
     */
    public void fillFormCreate(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(getCodeCtp());
        getCodeCtp().clear();
        getCodeCtp().sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(getDescriptionCtp());
        getDescriptionCtp().clear();
        getDescriptionCtp().sendKeys((String) data.get(Constants.DESCRIPTION));
        moveMouseAndClick(getAccCode());
        getAccCode().clear();
        getAccCode().sendKeys((String) data.get(Constants.ACCOUNTCCSIDE));
        moveMouseAndClick(driver.findElement(By.id("formId:accountingCodeId_selectLink")));
        moveMouseAndClick(driver.findElement(
            By.xpath("/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")));
        
        WebElement occCategoryEnum = driver.findElement(By.id("formId:accountingCodeId_selectLink"));
        moveMouseAndClick(occCategoryEnum);
        
        WebElement occCategoryEnum1 = driver.findElement(By.xpath("/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]"));
        moveMouseAndClick(occCategoryEnum1);
        
        
        WebElement occCategoryenumlabel =driver.findElement(By.xpath("/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div[5]/div/div/div[3]/span"));
        moveMouseAndClick(occCategoryenumlabel);
        
        WebElement occCategoryenumlabel1 = driver.findElement(By.id("formId:occCategory_enum_1"));
        moveMouseAndClick(occCategoryenumlabel1);
        
    }
    
    /**
     * search for the new created account Operation Type.
     * 
     * @param driver WebDriver
     * @param data code
     */
    
    public void fillFormAndSearch(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(codeSearch);
        codeSearch.clear();
        codeSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(searchBttnCtp);
    }
    
    /**
     * delete account Operation Type.
     * 
     * @param driver WebDriver
     * @throws InterruptedException
     */
    
    public void deleteOperation(WebDriver driver) throws InterruptedException {
        WebElement deleteRow = driver
            .findElement(By.id("datatable_results:0:code_id_message_link"));
        moveMouseAndClick(deleteRow);
        moveMouseAndClick(deleteBttn);
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
    }
    
    /**
     * @return the codeCtp
     */
    public final WebElement getCodeCtp() {
        return codeCtp;
    }
    
    /**
     * @param codeCtp the codeCtp to set
     */
    public final void setCodeCtp(WebElement codeCtp) {
        this.codeCtp = codeCtp;
    }
    
    /**
     * @return the accCode
     */
    public final WebElement getAccCode() {
        return accCode;
    }
    
    /**
     * @param accCode the accCode to set
     */
    public final void setAccCode(WebElement accCode) {
        this.accCode = accCode;
    }
    
    /**
     * @return the descriptionCtp
     */
    public final WebElement getDescriptionCtp() {
        return descriptionCtp;
    }
    
    /**
     * @param descriptionCtp the descriptionCtp to set
     */
    public final void setDescriptionCtp(WebElement descriptionCtp) {
        this.descriptionCtp = descriptionCtp;
    }
    
    /**
     * @return the codeSearch
     */
    public final WebElement getCodeSearch() {
        return codeSearch;
    }
    
    /**
     * @param codeSearch the codeSearch to set
     */
    public final void setCodeSearch(WebElement codeSearch) {
        this.codeSearch = codeSearch;
    }
    
}
