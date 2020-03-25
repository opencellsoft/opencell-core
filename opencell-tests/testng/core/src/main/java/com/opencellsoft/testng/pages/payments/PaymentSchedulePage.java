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
public class PaymentSchedulePage extends BasePage {
    
    public PaymentSchedulePage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * payment Menu.
     */
    @FindBy(id = "menu:payment")
    private WebElement paymentMenu;
    /**
     * directDebit Page.
     */
    @FindBy(id = "menu:paymentScheduleTemplates")
    private WebElement paymentScheduleTemplates;
    
    /**
     * code.
     */
    @FindBy(id = "formPaymentScheduleTemplate:tabView:code_txt")
    private WebElement code;
    
    /**
     * serviceTemplateList.
     */
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/div/div/div[4]/div/span/button/span[1]")
    private WebElement serviceTemplateList;
    
    /**
     * serviceTemplate.
     */
    @FindBy(xpath = "/html/body/div[10]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement serviceTemplate;
    
    /**
     * amount.
     */
    @FindBy(id = "formPaymentScheduleTemplate:tabView:amount_number")
    private WebElement amount;
    
    /**
     * paymentLabel.
     */
    @FindBy(id = "formPaymentScheduleTemplate:tabView:paymentLabel_txt")
    private WebElement paymentLabel;
    
    /**
     * calendarList.
     */
    @FindBy(id = "formPaymentScheduleTemplate:tabView:calendarId_label")
    private WebElement calendarList;
    
    /**
     * calendar.
     */
    @FindBy(id = "formPaymentScheduleTemplate:tabView:calendarId_4")
    private WebElement calendar;
    
    /**
     * ScheduledInvoiceTypeList.
     */
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/div/div/div[9]/div/span/button/span[2]")
    private WebElement ScheduledInvoiceTypeList;
    /**
     * invoiceTypeList.
     */
    @FindBy(xpath = "/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement ScheduledInvoiceType;
    
    /**
     * invoiceTypeList.
     */
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div/div/div/div[10]/div/span/button/span[1]")
    private WebElement ScheduledInvoiceCatList;
    
    @FindBy(xpath = "/html/body/div[11]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]")
    private WebElement ScheduledInvoiceCat;
    
    @FindBy(id = "formPaymentScheduleTemplate:tabView:paymentDayInMonth_number_input")
    private WebElement dayInMonth;
    
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[3]/div/div/button[1]/span[2]")
    private WebElement saveBtn;
    @FindBy(id = "searchForm:code_txt")
    private WebElement searchCode;
    
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div/div/div[3]/button[1]/span")
    private WebElement searchBtn;
    
    public void openPSPPage(final WebDriver driver) throws InterruptedException {
        moveMouseAndClick(paymentMenu);
        moveMouseAndClick(paymentScheduleTemplates);
    }
    public void fillPaymentScheduleAndSave(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement buttonNew = driver.findElement(
                    By.id("searchForm:buttonNew"));
                moveMouseAndClick(buttonNew);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        moveMouseAndClick(code);
        code.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(serviceTemplateList);
        moveMouseAndClick(serviceTemplate);
        moveMouseAndClick(amount);
        amount.clear();
        amount.sendKeys("20");
        moveMouseAndClick(paymentLabel);
        paymentLabel.clear();
        paymentLabel.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(calendarList);
        moveMouseAndClick(calendar);
        moveMouseAndClick(ScheduledInvoiceTypeList);
        moveMouseAndClick(ScheduledInvoiceType);
        moveMouseAndClick(ScheduledInvoiceCatList);
        moveMouseAndClick(ScheduledInvoiceCat);
        moveMouseAndClick(dayInMonth);
        dayInMonth.clear();
        dayInMonth.sendKeys("2");
        moveMouseAndClick(saveBtn);
    }
    public void searchPaymentScheduleAndDelete(final WebDriver driver, Map<String, String> data) {
        
        
        
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
    

