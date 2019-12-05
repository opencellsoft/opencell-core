package com.opencellsoft.testng.pages.payments;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
/**
 * 
 * @author Miftah
 *
 */
public class PaymentGatewayPage extends BasePage {
    
    public PaymentGatewayPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * payment Menu.
     */
    @FindBy(id = "menu:payment")
    private WebElement paymentMenu;
    /**
     * directDebit Page.
     */
    @FindBy(id = "menu:paymentGateways")
    private WebElement paymentGateways;
    
    /**
     * code.
     */
    @FindBy(id = "formPaymentGateway:tabView:code_txt")
    private WebElement code;
    
    /**
     * scriptInstanceList.
     */
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/div/div[1]/span[1]/div/div/span/button/span[1]")
    private WebElement scriptInstanceList;
    /**
     * pspTypeList.
     */
    @FindBy(id = "formPaymentGateway:tabView:type_enum_label")
    private WebElement pspTypeList;
    
    /**
     * pspTypeList.
     */
    @FindBy(id = "formPaymentGateway:tabView:type_enum_1")
    private WebElement pspType;
    
    /**
     * scriptInstance.
     */
    @FindBy(xpath = "/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement scriptInstance;
    
    /**
     * pMethodList.
     */
    @FindBy(id = "formPaymentGateway:tabView:paymentMethodType_enum_label")
    private WebElement pMethodList;
    
    /**
     * pMethod.
     */
    @FindBy(id = "formPaymentGateway:tabView:paymentMethodType_enum_1")
    private WebElement pMethod;
    /**
     * sellerList.
     */
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/div/div[1]/div[8]/div/span/button[1]/span[1]")
    private WebElement sellerList;
    
    /**
     * seller.
     */
    @FindBy(xpath = "/html/body/div[10]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement seller;
    
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[3]/div/div/button[1]/span[2]")
    private WebElement saveBtn;
    
    @FindBy(id = "searchForm:code_txt")
    private WebElement searchCode;
    
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div/div/div[3]/button[1]/span")
    private WebElement searchBtn;
    
    public void openPSPPage(final WebDriver driver) throws InterruptedException {
        moveMouseAndClick(paymentMenu);
        moveMouseAndClick(paymentGateways);
    }
    
    public void fillPaymentGatewayAndSave(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement buttonNew = driver.findElement(
                    By.xpath("/html/body/div[2]/form[2]/div/div/div/div[3]/button[3]/span"));
                moveMouseAndClick(buttonNew);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        moveMouseAndClick(code);
        moveMouseAndClick(code);
        code.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(scriptInstanceList);
        moveMouseAndClick(scriptInstance);
        moveMouseAndClick(pMethodList);
        moveMouseAndClick(pMethod);
        moveMouseAndClick(sellerList);
        moveMouseAndClick(seller);
        moveMouseAndClick(saveBtn);
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement backBtn = driver.findElement(By.xpath(
                    "/html/body/div[2]/form[2]/div/div[2]/div/div[3]/div/div/button[4]/span"));
                moveMouseAndClick(backBtn);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        moveMouseAndClick(searchCode);
        searchCode.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(searchBtn);
        
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
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement deletebttn = driver
                    .findElement(By.xpath("/html/body/div[2]/form[2]/div/div[2]/div/div[3]/div/div/button[5]/span[2]"));
                moveMouseAndClick(deletebttn);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
    }
}
