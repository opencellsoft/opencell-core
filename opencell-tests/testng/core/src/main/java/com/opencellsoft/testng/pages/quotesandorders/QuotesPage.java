package com.opencellsoft.testng.pages.quotesandorders;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class QuotesPage extends BasePage {

    public QuotesPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }
    /**
     * Quotes Menu.
     */
    @FindBy(id = "menu:orderAndQuotes")
    private WebElement quotesMenu;
    /**
     * Quotes Page.
     */
    @FindBy(id = "menu:quotes")
    private WebElement quotesPage;
    /**
     * button New
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement buttonNew;
    /**
     * codeQuote
     */   
    @FindBy(id = "quoteForm:tabView:quoteInfo:code_txt")
    private WebElement codeQuote;
    /**
     * codeQuote
     */   
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/ul/li[2]/a")
    private WebElement lineTab;
    
    
    /**
     * @return instance of CreateNewQuotesPage
     * @throws InterruptedException 
     */
    public void openQuotesList() throws InterruptedException {
        moveMouse(quotesMenu);
        moveMouseAndClick(quotesPage);
    }
    public void fillQuotes(final WebDriver driver ,Map<String, String> data) throws InterruptedException {
        moveMouseAndClick(buttonNew);
        moveMouseAndClick(codeQuote);
        codeQuote.clear();
        codeQuote.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(lineTab);
        WebElement addLine = driver.findElements(By.className("ui-button-text-only")).get(0);
        moveMouseAndClick(addLine);
        WebElement addAccount = driver.findElement(By.id("quoteForm:tabView:userAccount_selectLink"));
        moveMouseAndClick(addAccount);;
        WebElement account = driver.findElement(By.xpath("/html/body/div[10]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]"));
        moveMouseAndClick(account);
        WebElement addProduct = driver.findElement(By.xpath("/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/div/div[2]/span/div/div[2]/div[1]/div[3]/div/span/button/span[1]"));
        moveMouseAndClick(addProduct);
        
        WebElement codeProduct = driver.findElement(By.id("offerPopuppopupForm:searchField1"));
        moveMouseAndClick(codeProduct);
        codeProduct.clear();
        codeProduct.sendKeys("PR_CLASSIC_DEFAULT");
        
        WebElement productSearch = driver.findElement(
            By.xpath("/html/body/div[12]/div[2]/form/div[1]/div[3]/button[1]/span"));
        moveMouseAndClick(productSearch);
        

        WebElement product = driver.findElement(
            By.xpath("/html/body/div[12]/div[2]/form/div[2]/div[2]/table/tbody/tr/td[1]"));
        moveMouseAndClick(product);

        WebElement saveQuoteLine = driver.findElement(By.xpath("/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/div/div[2]/span/div/div[2]/div[2]/button[1]/span"));
        moveMouseAndClick(saveQuoteLine);
    }
    public void saveQuotes(final WebDriver driver) throws InterruptedException {
        WebElement save = driver.findElement(By.id("quoteForm:formButtonsCC:saveButtonAjax"));
        moveMouseAndClick(save);
    }
    public void searchQuotesAndDelete(final WebDriver driver ,Map<String, String> data) throws InterruptedException {
        //Code to Search 
        WebElement searchFormCode = driver.findElement(By.id("searchForm:code_txt"));
        moveMouseAndClick(searchFormCode);
        searchFormCode.clear();
        searchFormCode.sendKeys((String) data.get(Constants.CODE));
        //Click on search Btn
        WebElement searchBtn = driver.findElement(By.id("searchForm:buttonSearch"));
        moveMouseAndClick(searchBtn);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement rowToDelete = driver
                    .findElement(By.id("datatable_results:0:code_id_message_link"));
                moveMouseAndClick(rowToDelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement deleteBtn = driver
                    .findElement(By.id("quoteForm:formButtonsCC:deletelink"));
                moveMouseAndClick(deleteBtn);
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
     * @return the quotesMenu
     */
    public WebElement getQuotesMenu() {
        return quotesMenu;
    }
    /**
     * @param quotesMenu the quotesMenu to set
     */
    public void setQuotesMenu(WebElement quotesMenu) {
        this.quotesMenu = quotesMenu;
    }
    /**
     * @return the quotesPage
     */
    public WebElement getQuotesPage() {
        return quotesPage;
    }
    /**
     * @param quotesPage the quotesPage to set
     */
    public void setQuotesPage(WebElement quotesPage) {
        this.quotesPage = quotesPage;
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
     * @return the codeQuote
     */
    public WebElement getCodeQuote() {
        return codeQuote;
    }
    /**
     * @param codeQuote the codeQuote to set
     */
    public void setCodeQuote(WebElement codeQuote) {
        this.codeQuote = codeQuote;
    }
    /**
     * @return the lineTab
     */
    public WebElement getLineTab() {
        return lineTab;
    }
    /**
     * @param lineTab the lineTab to set
     */
    public void setLineTab(WebElement lineTab) {
        this.lineTab = lineTab;
    }

}
