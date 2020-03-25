package com.opencellsoft.testng.pages.quotesandorders;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class OrdersPage extends BasePage {
    
    /**
     * Quotes Menu.
     */
    @FindBy(id = "menu:orderAndQuotes")
    private WebElement ordersMenu;
    /**
     * Orders Page.
     */
    @FindBy(id = "menu:orders")
    private WebElement ordersPage;
    /**
     * button New
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement buttonNew;

    
    /**
     * alia
     */
    @FindBy(id = "orderForm:tabView:orderInfo:paymentMethod_alias_txt")
    private WebElement alia;
    
    /**
     * line Tab
     */
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/ul/li[2]/a")
    private WebElement lineTab;
    
    public OrdersPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * @return instance of CreateNewQuotesPage
     * @throws InterruptedException
     */
    public void openOrdersList() throws InterruptedException {
        moveMouse(ordersMenu);
        moveMouseAndClick(ordersPage);
    }
    
    public void fillOrders(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(buttonNew);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement codeOrder = driver
                    .findElement(By.id("orderForm:tabView:orderInfo:code_txt"));
                moveMouseAndClick(codeOrder);
                codeOrder.clear();
                codeOrder.sendKeys((String) data.get(Constants.CODE));
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }

        moveMouseAndClick(alia);
        alia.clear();
        alia.sendKeys("12056");
        moveMouseAndClick(lineTab);
        WebElement addLine = driver.findElements(By.className("ui-button-text-only")).get(2);
        moveMouseAndClick(addLine);
        WebElement actionList = driver.findElement(By.xpath(
            "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/div/div[2]/span/div/div[2]/div[1]/div[2]/div/div/div[3]/span"));
        moveMouseAndClick(actionList);
        WebElement action = driver.findElement(By.id("orderForm:tabView:action_enum_1"));
        moveMouseAndClick(action);
        WebElement accountList = driver
            .findElement(By.id("orderForm:tabView:userAccount_selectLink"));
        moveMouseAndClick(accountList);
        WebElement account = driver.findElement(
            By.xpath("/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]"));
        moveMouseAndClick(account);
        
        WebElement offerSelect = driver.findElement(
            By.xpath("/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/div/div[2]/span/div/div[2]/div[1]/div[4]/div/span/button/span[1]"));
        moveMouseAndClick(offerSelect);
        WebElement offerCode = driver.findElement(
            By.id("offerPopuppopupForm:searchField1"));
        offerCode.click();
        offerCode.clear();
        offerCode.sendKeys("OF_CLASSIC_TEMPLATE");        
        WebElement offerSearch = driver.findElement(
            By.xpath("/html/body/div[11]/div[2]/form/div[1]/div[3]/button[1]/span"));
        moveMouseAndClick(offerSearch);        
        WebElement offer = driver.findElement(
            By.xpath("/html/body/div[11]/div[2]/form/div[2]/div[2]/table/tbody/tr/td[1]"));
        moveMouseAndClick(offer);

        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement servicetoActivate = driver
                    .findElement(By.xpath("/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/div/div[2]/span/div/div[2]/div[1]/span[1]/div/div/div/ul/li/ul/li[1]/ul/li[1]/span/span[3]/div/div[2]/span"));
                moveMouseAndClick(servicetoActivate);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        WebElement sellerList = driver.findElement(
            By.id("orderForm:tabView:configTab:seller_0_label"));
        moveMouseAndClick(sellerList);
        WebElement seller = driver.findElement(
            By.id("orderForm:tabView:configTab:seller_0_3"));
        moveMouseAndClick(seller);
        WebElement saveOrderLine = driver.findElements(By.className("ui-button-text-only")).get(5);
        moveMouseAndClick(saveOrderLine);
    }
    
    public void saveOrders(final WebDriver driver) throws InterruptedException {
        WebElement save = driver.findElement(By.id("orderForm:formButtonsCC:saveButtonAjax"));
        moveMouseAndClick(save);
    }
    
    public void searchOrdersAndDelete(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        // Code to Search
        WebElement searchFormCode = driver.findElement(By.id("searchForm:code_txt"));
        moveMouseAndClick(searchFormCode);
        searchFormCode.clear();
        searchFormCode.sendKeys((String) data.get(Constants.CODE));
        // Click on search Btn
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
                    .findElement(By.id("orderForm:formButtonsCC:deletelink"));
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
     * @return the ordersMenu
     */
    public WebElement getOrdersMenu() {
        return ordersMenu;
    }

    /**
     * @param ordersMenu the ordersMenu to set
     */
    public void setOrdersMenu(WebElement ordersMenu) {
        this.ordersMenu = ordersMenu;
    }

    /**
     * @return the ordersPage
     */
    public WebElement getOrdersPage() {
        return ordersPage;
    }

    /**
     * @param ordersPage the ordersPage to set
     */
    public void setOrdersPage(WebElement ordersPage) {
        this.ordersPage = ordersPage;
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
