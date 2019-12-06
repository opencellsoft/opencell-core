package com.opencellsoft.testng.pages.customers;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class SubscriptionPage extends BasePage {
    
    @FindBy(id = "subscriptionTab:subscriptionFormId:informationTab:userSelectId_selectLink")
    private WebElement userAccountSelect;
    
    @FindBy(xpath = "/html/body/div[10]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement userAccount;
    
    @FindBy(id = "subscriptionTab:subscriptionFormId:informationTab:offerSelectId_selectLink")
    private WebElement offerSelect;
    
    @FindBy(xpath = "/html/body/div[11]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement offer;
    
    @FindBy(id = "subscriptionTab:subscriptionFormId:informationTab:code_txt")
    private WebElement codeSubscription;
    
    @FindBy(id = "subscriptionTab:subscriptionFormId:informationTab:sellerSelectId_selectLink")
    private WebElement sellerSelect;
    
    @FindBy(xpath = "/html/body/div[14]/div[2]/form/div[2]/div[2]/table/tbody/tr[3]/td[1]")
    private WebElement seller;
    
    @FindBy(id = "subscriptionTab:subscriptionFormId:formButtonsCC:saveButtonAjax")
    private WebElement saveBtn;
    
    public SubscriptionPage(WebDriver driver) {
        super(driver);
    }
    
    public void gotoListPage(WebDriver driver) {
        WebElement customersMenu = driver.findElement(By.id("menu:crm"));
        moveMouse(customersMenu);
        
        WebElement subbscription = driver.findElement(By.id("menu:subscriptions"));
        moveMouseAndClick(subbscription);
        
    }
    
    public void fillFormSubscription(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
        userAccountSelect.click();
        moveMouseAndClick(userAccount);
        moveMouseAndClick(offerSelect);
        moveMouseAndClick(offer);
        moveMouseAndClick(sellerSelect);
        moveMouseAndClick(seller);
        moveMouseAndClick(codeSubscription);
        codeSubscription.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(saveBtn);
        WebElement servicetab = driver.findElement(By.xpath("/html/body/div[2]/span/span[2]/div/ul/li[1]/a"));
        moveMouseAndClick(servicetab);
        WebElement service1 = driver.findElement(By.xpath("/html/body/div[2]/span/span[2]/div/div/div[1]/form[3]/div/div/div[1]/table/tbody/tr[1]/td[1]/div/div[2]/span"));
        moveMouseAndClick(service1);
        WebElement instantiate = driver.findElement(By.id("subscriptionTab:cbutton"));
        moveMouseAndClick(instantiate);
        WebElement serviceInstance1 = driver.findElement(By.xpath(
            "/html/body/div[2]/span/span[2]/div/div/div[1]/form[2]/div/div[1]/table/tbody/tr[1]/td[1]/div/div[2]/span"));
        moveMouseAndClick(serviceInstance1);
        WebElement activate = driver.findElements(By.className("ui-button-text-only")).get(0);
        moveMouseAndClick(activate);
        WebElement confirm = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirm);
       
        
    }
}
