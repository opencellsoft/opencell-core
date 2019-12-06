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
public class FilterPage extends BasePage {
    
    /**
     * code.
     */
    @FindBy(id = "tabView:formId:code_txt")
    private WebElement code;
    
    /**
     * description.
     */
    @FindBy(id = "tabView:formId:description_txt")
    private WebElement description;
    
    /**
     * shared button .
     */
    @FindBy(id = "tabView:formId:shared_bool")
    private WebElement shared;
    
    /**
     * Input XML .
     */
    @FindBy(xpath = "/html/body/div[2]/div/div[2]/div/div/div/form/div/div[2]/div/div[2]/div[4]/div/div/div[1]/textarea")
    
    private WebElement inputXml;
    /**
     * button search.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBttn;
    
    /**
     * button save.
     */
    @FindBy(id = "tabView:formId:formButtonsCC:saveButton")
    private WebElement savebttn;
    
    /**
     * code search.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeSearch;
    
    /**
     * button delete.
     */
    @FindBy(id = "datatable_results:0:resultsdeletelink")
    private WebElement deletebttn;
    
    /**
     * button New.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement bttnNew;
    
    /**
     * 
     * @param driver instance of WebDriver
     */
    public FilterPage(final WebDriver driver) {
        super(driver);
        
    }
    
    /**
     * click on administration -> Filters.
     * 
     * @param driver WebDriver
     * 
     */
    public void gotoListPage(final WebDriver driver) {
        
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
        
        WebElement filtersMenu = driver.findElement(By.id("menu:filters"));
        moveMouseAndClick(filtersMenu);
        
    }
    
    /**
     * @param driver instance of WebDriver
     * @param element element to send keys
     * @param keysToSend string to be sent.
     */
    public void forceSendkeys(WebDriver driver, WebElement element, String keysToSend) {
        Actions action = new Actions(driver);
        action.moveToElement(element).click().sendKeys(keysToSend).perform();
    }
    
    /**
     * fill the new filter.
     * 
     * @param driver instance of WebDriver
     * @param data Map
     */
    public void fillFormCreate(WebDriver driver, Map<String, String> data) {
        
        String s = "<filter><disabled>false</disabled><appendGeneratedCode>false</appendGeneratedCode>\r\n"
                + "     <filterCondition class=\"andCompositeFilterCondition\">\r\n"
                + "<filterConditionType>COMPOSITE_AND</filterConditionType>\r\n"
                + "<filterConditions>\r\n" + "<primitiveFilterCondition>\r\n"
                + "<filterConditionType>PRIMITIVE</filterConditionType>\r\n"
                + "<fieldName>occ.code</fieldName>\r\n"
                + "<operator>=</operator><operand>INV_STD</operand>\r\n"
                + " </primitiveFilterCondition>\r\n"
                + "   </filterConditions></filterCondition>\r\n"
                + "     <primarySelector><targetEntity>org.meveo.model.payments.OCCTemplate</targetEntity>\r\n"
                + "       <alias>occ</alias><displayFields>\r\n"
                + "         <field>occ.code</field>\r\n"
                + "        <field>occ.description</field></displayFields>\r\n"
                + " <exportFields/> <ignoreIfNotFoundForeignKeys/>\r\n"
                + "     </primarySelector><secondarySelectors/>\r\n" + "    </filter>";
        moveMouseAndClick(bttnNew);
        // Code
        moveMouseAndClick(getCode());
        getCode().clear();
        getCode().sendKeys((String) data.get(Constants.CODE));
        // Description
        moveMouseAndClick(getDescription());
        getDescription().clear();
        getDescription().sendKeys((String) data.get(Constants.DESCRIPTION));
        // check shared
        moveMouseAndClick(shared);
        // Input XML
        moveMouseAndClick(inputXml);
        inputXml.sendKeys(s);
        moveMouseAndClick(savebttn);
        moveMouseAndClick(codeSearch);
        codeSearch.clear();
        codeSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(searchBttn);
        moveMouseAndClick(deletebttn);
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(yes);
    }
    
    /**
     * @return the code
     */
    public final WebElement getCode() {
        return code;
    }
    
    /**
     * @param code the code to set
     */
    public final void setCode(WebElement code) {
        this.code = code;
    }
    
    /**
     * @return the description
     */
    public final WebElement getDescription() {
        return description;
    }
    
    /**
     * @param description the description to set
     */
    public final void setDescription(WebElement description) {
        this.description = description;
    }
    
}
