package com.opencellsoft.testng.pages.administration;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * @author HASSNAA MIFTAH
 *
 */
public class ScriptPage extends BasePage {
    
    /**
     * description.
     */
    @FindBy(id = "scriptInstanceForm:description_txt")
    private WebElement description;
    
    /**
     * code.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement code;
    
    /**
     * search button.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBtn;

    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public ScriptPage(WebDriver driver) {
        super(driver);
        
    }
    
    /**
     * go to administration -> script Instances.
     * 
     * @param driver WebDriver
     */
    public void gotoListPage(WebDriver driver) {
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement adminstrationMenu = driver
                    .findElement(By.id("menu:automation"));
                moveMouseAndClick(adminstrationMenu);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        WebElement jobsMenu = driver.findElement(By.id("menu:adminJobs"));
        moveMouseAndClick(jobsMenu);
        WebElement scriptMenu = driver.findElement(By.id("menu:scriptInstances"));
        moveMouseAndClick(scriptMenu);
    }

    public void forceSendkeys(WebDriver driver, WebElement element, String keysToSend) {
        Actions action = new Actions(driver);
        action.moveToElement(element).click().sendKeys(keysToSend).perform();
    }
    
    /**
     * fill form with random values .
     * 
     * @param driver WebDriver
     * @param data Map
     * @throws InterruptedException 
     */
    public void fillForm(WebDriver driver, Map<String, String> data) throws InterruptedException {
        WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
        btnNew.click();
        String s = "package com."+(String) data.get(Constants.CODE)+";\n" + "import org.meveo.service.script.Script;\n"
                + "import java.util.Map;\n" + "public class "+ (String) data.get(Constants.CODE) +" extends Script {\n"
                + "@Override\n" + "public void execute(Map<String, Object> initContext) {\n" + "}\n"
                + " }";
        description.click();
        description.clear();
        description.sendKeys("this is a description");
        
        WebElement scriptfield = driver.findElement(By.xpath(
            "/html/body/div[2]/form/div/div[2]/div/div[2]/div[5]/div/div[1]/div[1]/textarea"));
        moveMouseAndClick(scriptfield);
        scriptfield.sendKeys(s);
        WebElement validateCompile = driver.findElements(By.tagName("button")).get(0);
        moveMouseAndClick(validateCompile);
        WebElement btnSave = driver.findElements(By.className("ui-button-text-icon-left")).get(0);
        moveMouseAndClick(btnSave);    
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys("com."+(String) data.get(Constants.CODE)+"."+(String) data.get(Constants.CODE));
        moveMouseAndClick(searchBtn);
        
        for(int i = 0; i < 2; i++) 
        {
         try

        {
         WebElement deletebttn = driver.findElement(By.xpath("/html/body/div[2]/div/div[1]/form/div[1]/div[2]/table/tbody/tr[1]/td[7]/button/span[1]"));
         moveMouseAndClick(deletebttn);
         break;
         }

        catch(StaleElementReferenceException see)

        {
         }
         }
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
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
     * @return the searchBtn
     */
    public WebElement getSearchBtn() {
        return searchBtn;
    }

    /**
     * @param searchBtn the searchBtn to set
     */
    public void setSearchBtn(WebElement searchBtn) {
        this.searchBtn = searchBtn;
    }
}
