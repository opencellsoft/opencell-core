package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Contact page.
 * 
 * @author Fatine BELHADJ
 * 
 *
 */
public class ContactPage extends BasePage {
    /**
     * new code.
     */
    @FindBy(id = "formId:code_txt")
    private WebElement codeCp;
    /**
     * new description.
     */
    @FindBy(id = "formId:description_txt")
    private WebElement descriptionCp;
    /**
     * new email.
     */
    @FindBy(id = "formId:email_txt")
    private WebElement emailCp;
    /**
     * searching email.
     */
    @FindBy(id = "searchForm:email_txt")
    public WebElement  emailsearch;
    /**
     * generic mail.
     */
    @FindBy(id = "formId:genericMail_txt")
    private WebElement genericMailCp;
    /**
     * new phone.
     */
    @FindBy(id = "formId:phone_txt")
    private WebElement phoneCp;
    /**
     * mobile .
     */
    @FindBy(id = "formId:mobile_txt")
    private WebElement mobileCp;

    /**
     * button delete.
     */
    @FindBy(id = "formId:formButtonsCC:deletelink")
    private WebElement delete;
    /**
     * button yes.
     */
    @FindBy(css = ".ui-button")
    private WebElement yes;
    
    /**
     * constructor.
     * 
     * @param driver constructor
     */
    public ContactPage(final WebDriver driver) {
        super(driver);
        
    }
    
    /**
     * Opening contact page.
     * 
     * @param driver contact
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);
        
        WebElement contactMenu = driver.findElement(By.id("menu:providerContacts"));
        moveMouseAndClick(contactMenu);
        
    }
    
    /**
     * deleteselected data.
     * 
     * @param driver WebDriver
     * @param data contact data
     */
    public void gotoBtnSearchDelete(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(emailsearch);
        emailsearch.clear();
        emailsearch.sendKeys((String) data.get(Constants.EMAIL));
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
        moveMouseAndClick(delete);
        moveMouseAndClick(yes);
    }
    
    /**
     * Entering data.
     * 
     * @param driver new contact.
     * @param data contact
     * @throws InterruptedException
     */
    public void fillFormAndSave(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        moveMouseAndClick(btnNew);
        moveMouseAndClick(codeCp);
        codeCp.clear();
        codeCp.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(descriptionCp);
        descriptionCp.clear();
        descriptionCp.sendKeys((String) data.get(Constants.DESCRIPTION));
        moveMouseAndClick(emailCp);
        emailCp.clear();
        emailCp.sendKeys((String) data.get(Constants.EMAIL));
        moveMouseAndClick(genericMailCp);
        genericMailCp.clear();
        genericMailCp.sendKeys((String) data.get(Constants.GENERIC_EMAIL));
        moveMouseAndClick(phoneCp);
        phoneCp.clear();
        phoneCp.sendKeys((String) data.get(Constants.PHONE));
        moveMouseAndClick(mobileCp);
        mobileCp.clear();
        mobileCp.sendKeys((String) data.get(Constants.MOBILE));
        WebElement btnSave = driver.findElement(By.id("formId:formButtonsCC:saveButton"));
        moveMouseAndClick(btnSave);
        moveMouseAndClick(emailsearch);
        emailsearch.clear();
        emailsearch.sendKeys((String) data.get(Constants.EMAIL));
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
        WebElement delete =  driver.findElement(By.id("formId:formButtonsCC:deletelink"));
        moveMouseAndClick(delete);
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
        
    }
    
    /**
     * code getter.
     * 
     * @return code
     */
    public WebElement getcodeCp() {
        return this.codeCp;
    }
    
    /**
     * code setter.
     * 
     * @param codeCp setter
     */
    public void setcodeCp(WebElement codeCp) {
        this.codeCp = codeCp;
    }
    
    /**
     * description setter.
     * 
     * @param descriptionCp setter
     */
    public void setdescriptionCp(WebElement descriptionCp) {
        this.descriptionCp = descriptionCp;
    }
    
    /**
     * description getter.
     * 
     * @return getter
     */
    public WebElement getdescriptionCp() {
        return this.descriptionCp;
    }
    
    /**
     * email setter.
     * 
     * @param emailCp setter
     */
    public void setemailCp(WebElement emailCp) {
        this.emailCp = emailCp;
    }
    
    /**
     * email getter.
     * 
     * @return email
     */
    public WebElement getemailCp() {
        return this.emailCp;
    }
    
    /**
     * generic mail setter.
     * 
     * @param genericMailCp setter
     */
    public void setgenericMailCp(WebElement genericMailCp) {
        this.genericMailCp = genericMailCp;
    }
    
    /**
     * generic mail getter.
     * 
     * @return generic mail
     */
    public WebElement getgenericMailCp() {
        return this.genericMailCp;
    }
    
    /**
     * phone setter.
     * 
     * @param phoneCp setter
     */
    public void setphoneCp(WebElement phoneCp) {
        this.phoneCp = phoneCp;
    }
    
    /**
     * phone getter.
     * 
     * @return phone
     */
    public WebElement getphoneCp() {
        return this.phoneCp;
    }
    
    /**
     * mobile setter.
     * 
     * @param mobileCp setter
     */
    public void setmobileCp(WebElement mobileCp) {
        this.mobileCp = mobileCp;
    }
    
    /**
     * mobile getter.
     * 
     * @return mobile
     */
    public WebElement getmobileCp() {
        return this.mobileCp;
    }
}
