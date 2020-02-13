package com.opencellsoft.testng.pages.setup;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Maria AIT BRAHIM
 *
 */
public class AssuredFactorPage extends BasePage {
    
    public AssuredFactorPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * code.
     */
    @FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[1]/div/div[1]/div/div/input")
    private WebElement codeAF;
    
    /**
     * description.
     */
    @FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[1]/div/div[2]/div/div/input")
    private WebElement descriptionAF;
    
    /**
     * save button.
     */
    @FindBy(xpath = "/html/body/div[3]/div[2]/div/div/form/div[2]/div/button/span[1]")
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
     * click on setup -> Assured factors .
     * 
     * @param driver instance of WebDriver
     * @throws InterruptedException
     */
    public void gotoListPage(WebDriver driver) throws InterruptedException {
        WebElement setupMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/a[2]"));
        moveMouse(setupMenu);
        
        WebElement AssuredFactorMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/div[10]/div/div/a[5]"));
        moveMouseAndClick(AssuredFactorMenu);
    }
    
    /**
     * Go to New Assured factors page.
     * 
     * @param driver instance of WebDriver
     */
    public void gotoNewPage(WebDriver driver) {
        WebElement btnNew = driver.findElement(By.xpath(
            "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div/a/span[1]/span"));
        
        forceClick(btnNew);
    }
    
    /**
     * the button save.
     * 
     * @param driver WebDriver
     */
    
    public void saveOperation(WebDriver driver) {
        WebElement btnSave = driver.findElement(
            By.xpath("/html/body/div[3]/div[2]/div/div/form/div[2]/div/button/span[1]"));
        
        moveMouseAndClick(btnSave);
    }
    
    /**
     * fill the new Assured factors.
     * 
     * @param driver WebDriver
     * @param data Map
     * @throws InterruptedException
     */
    public void fillFormCreate(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        
        moveMouseAndClick(getCodeAF());
        getCodeAF().clear();
        getCodeAF().sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(getDescriptionAF());
        getDescriptionAF().clear();
        getDescriptionAF().sendKeys((String) data.get(Constants.DESCRIPTION));
        
    }
    
    public WebElement getCodeAF() {
        return codeAF;
    }
    
    public void setCodeAF(WebElement codeAF) {
        this.codeAF = codeAF;
    }
    
    public WebElement getDescriptionAF() {
        return descriptionAF;
    }
    
    public void setDescriptionAF(WebElement descriptionAF) {
        this.descriptionAF = descriptionAF;
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
