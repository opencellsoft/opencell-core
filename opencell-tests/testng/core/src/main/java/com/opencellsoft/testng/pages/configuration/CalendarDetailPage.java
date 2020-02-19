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
 * 
 * @author Maria AIT BRAHIM
 */

public class CalendarDetailPage extends BasePage {
    
    /**
     * code.
     */
    @FindBy(id = "formId:code_txt")
    private WebElement codeCdp;
    
    /**
     * description.
     */
    @FindBy(id = "formId:description")
    private WebElement descriptionCdp;
    
    /**
     * calendar type research.
     */
    @FindBy(css = "#formId\\:calendarType")
    private WebElement calendarType1;
    
    /**
     * calendar type.
     */
    @FindBy(id = "formId:calendarType_1")
    private WebElement calendarType;
    
    /**
     * button save.
     */
    @FindBy(id = "formId:formButtonsCC:saveButton")
    private WebElement bttnSave;
    
    /**
     * code search.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeSearch;
    
    /**
     * button search.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBttn;
    
    /**
     * button delete.
     */
    @FindBy(id = "formId:formButtonsCC:deletelink")
    private WebElement deleteBttn;
    
    /**
     * CalendarDetailPage constructor.
     * 
     * @param driver WebDriver
     */
    public CalendarDetailPage(final WebDriver driver) {
        super(driver);
        
    }
    
    /**
     * click on configuration -> calendars.
     * 
     * @param driver WebDriver
     */
    public void gotoListPage(WebDriver driver) {
        
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);
        
        WebElement calendarSubMenu = driver.findElement(By.id("menu:calendars"));
        moveMouseAndClick(calendarSubMenu);
        
    }
    
    /**
     * Go to New Calendar page.
     * 
     * @param driver WebDriver
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
    
    public void saveCalendar(WebDriver driver) {
        moveMouseAndClick(bttnSave);
    }
    
  /**
   * create new calendar type.
   * 
   * @param driver WebDriver
   * @param data Map
   */
  public void fillFormCreate(WebDriver driver, Map<String, String> data) {
    // Calendar Type
    moveMouseAndClick(calendarType1);
    moveMouseAndClick(calendarType);
    // Code
    moveMouseAndClick(codeCdp);
    codeCdp.clear();
    codeCdp.sendKeys((String) data.get(Constants.CODE));
    // Dscription
    moveMouseAndClick(descriptionCdp);
    descriptionCdp.clear();
    descriptionCdp.sendKeys((String) data.get(Constants.DESCRIPTION));

  }
    
    /**
     * search for the new created calendar.
     * 
     * @param driver WebDriver
     * @param data code
     */
    public void fillFormAndSearch(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(codeSearch);
        codeSearch.clear();
        codeSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(searchBttn);
        
    }
    
    /**
     * delete calendar.
     * 
     * @param driver WebDriver
     * @throws InterruptedException
     */
    public void deleteCalendar(WebDriver driver) throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement chartToDelete = driver
                    .findElement(By.id("datatable_results:0:code_id_message_link"));
                moveMouseAndClick(chartToDelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        moveMouseAndClick(deleteBttn);
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
        
    }
    
    /**
     * @return the codeCdp
     */
    public final WebElement getCodeCdp() {
        return codeCdp;
    }
    
    /**
     * @param codeCdp the codeCdp to set
     */
    public final void setCodeCdp(WebElement codeCdp) {
        this.codeCdp = codeCdp;
    }
    
    /**
     * @return the descriptionCdp
     */
    public final WebElement getDescriptionCdp() {
        return descriptionCdp;
    }
    
    /**
     * @param descriptionCdp the descriptionCdp to set
     */
    public final void setDescriptionCdp(WebElement descriptionCdp) {
        this.descriptionCdp = descriptionCdp;
    }
    
    /**
     * @return the calendarType1
     */
    public final WebElement getCalendarType1() {
        return calendarType1;
    }
    
    /**
     * @param calendarType1 the calendarType1 to set
     */
    public final void setCalendarType1(WebElement calendarType1) {
        this.calendarType1 = calendarType1;
    }
    
    /**
     * @return the calendarType
     */
    public final WebElement getCalendarType() {
        return calendarType;
    }
    
    /**
     * @param calendarType the calendarType to set
     */
    public final void setCalendarType(WebElement calendarType) {
        this.calendarType = calendarType;
    }
    
    /**
     * @return the bttnSave
     */
    public final WebElement getBttnSave() {
        return bttnSave;
    }
    
    /**
     * @param bttnSave the bttnSave to set
     */
    public final void setBttnSave(WebElement bttnSave) {
        this.bttnSave = bttnSave;
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
    
    /**
     * @return the searchBttn
     */
    public final WebElement getSearchBttn() {
        return searchBttn;
    }
    
    /**
     * @param searchBttn the searchBttn to set
     */
    public final void setSearchBttn(WebElement searchBttn) {
        this.searchBttn = searchBttn;
    }
    
    /**
     * @return the deleteBttn
     */
    public final WebElement getDeleteBttn() {
        return deleteBttn;
    }
    
    /**
     * @param deleteBttn the deleteBttn to set
     */
    public final void setDeleteBttn(WebElement deleteBttn) {
        this.deleteBttn = deleteBttn;
    }
    
}
