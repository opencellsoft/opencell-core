package com.opencellsoft.testng.pages.reporting;
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
public class MQPage extends BasePage {
    
    /**
     * menuReporting Menu.
     */
    @FindBy(id = "menu:reporting")
    private WebElement menuReporting;
    /**
     * menuMeasurableQuantities Page.
     */
    @FindBy(id = "menu:measurableQuantities")
    private WebElement menuMeasurableQuantities;

    /**
     * dimension1.
     */
    @FindBy(id = "formId:dimension1_txt")
    private WebElement dimension1;
    /**
     * dimension2.
     */
    @FindBy(id = "formId:dimension2_txt")
    private WebElement dimension2;
    /**
     * dimension3.
     */
    @FindBy(id = "formId:dimension3_txt")
    private WebElement dimension3;
    /**
     * measurementPeriodList.
     */
    @FindBy(xpath = "/html/body/div[2]/form[2]/div/div[2]/div/div[2]/div[8]/div/div/div[3]/span")
    private WebElement measurementPeriodList;
    /**
     * measurementPeriod.
     */
    @FindBy(id = "formId:measurementPeriod_enum_1")
    private WebElement measurementPeriod;
    
    /**
     * saveMq.
     */
    @FindBy(id = "formId:formButtonsCC:saveButton")
    private WebElement saveMq;
    
    /**
     * mQToSearch.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement mQToSearch;
    /**
     * mQToSearch.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement buttonSearch;

    /**
     * chartToDelete.
     */
    @FindBy(id = "formId:formButtonsCC:deletelink")
    private WebElement deleteBtn;
    
    public MQPage(WebDriver driver) {
        super(driver);
    }
    
    public void openMQList(final WebDriver driver) throws InterruptedException {
       
        moveMouse(menuReporting);
       
        moveMouseAndClick(menuMeasurableQuantities);
    }
    public void saveMQ (final WebDriver driver)throws InterruptedException {
     
        moveMouseAndClick(saveMq);
    }
    public void fillMQ(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {        
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement buttonNew = driver.findElement(By.xpath("/html/body/div[2]/form[2]/div/div/div/div[3]/button[3]/span"));
                moveMouseAndClick(buttonNew);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement code = driver.findElement(By.id("formId:code_txt"));
                moveMouseAndClick(code);
                code.clear();
                code.sendKeys((String) data.get(Constants.CODE));
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }

   
        moveMouseAndClick(dimension1);
        dimension1.clear();
        dimension1.sendKeys((String) data.get(Constants.CODE));
     
        moveMouseAndClick(dimension2);
        dimension2.clear();
        dimension2.sendKeys((String) data.get(Constants.CODE));
       
        moveMouseAndClick(dimension3);
        dimension3.clear();
        dimension3.sendKeys((String) data.get(Constants.CODE));
  
        moveMouseAndClick(measurementPeriodList);
       
        moveMouseAndClick(measurementPeriod);
        
    }
    
    public void searchMQAndDelete(final WebDriver driver, Map<String, String> data)
            throws InterruptedException {
       
        moveMouseAndClick(mQToSearch);
        mQToSearch.clear();
        mQToSearch.sendKeys((String) data.get(Constants.CODE));
      
        moveMouseAndClick(buttonSearch);
        
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement mQToDelete = driver
                    .findElement(By.id("datatable_results:0:code_id_message_link"));
                moveMouseAndClick(mQToDelete);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
      
        moveMouseAndClick(deleteBtn);
        /**
         * click on confirm button.
         */
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
      
        confirmDelete.click();
    }

    /**
     * @return the menuReporting
     */
    public WebElement getMenuReporting() {
        return menuReporting;
    }

    /**
     * @param menuReporting the menuReporting to set
     */
    public void setMenuReporting(WebElement menuReporting) {
        this.menuReporting = menuReporting;
    }

    /**
     * @return the menuMeasurableQuantities
     */
    public WebElement getMenuMeasurableQuantities() {
        return menuMeasurableQuantities;
    }

    /**
     * @param menuMeasurableQuantities the menuMeasurableQuantities to set
     */
    public void setMenuMeasurableQuantities(WebElement menuMeasurableQuantities) {
        this.menuMeasurableQuantities = menuMeasurableQuantities;
    }

   
    /**
     * @return the dimension1
     */
    public WebElement getDimension1() {
        return dimension1;
    }

    /**
     * @param dimension1 the dimension1 to set
     */
    public void setDimension1(WebElement dimension1) {
        this.dimension1 = dimension1;
    }

    /**
     * @return the dimension2
     */
    public WebElement getDimension2() {
        return dimension2;
    }

    /**
     * @param dimension2 the dimension2 to set
     */
    public void setDimension2(WebElement dimension2) {
        this.dimension2 = dimension2;
    }

    /**
     * @return the dimension3
     */
    public WebElement getDimension3() {
        return dimension3;
    }

    /**
     * @param dimension3 the dimension3 to set
     */
    public void setDimension3(WebElement dimension3) {
        this.dimension3 = dimension3;
    }

    /**
     * @return the measurementPeriodList
     */
    public WebElement getMeasurementPeriodList() {
        return measurementPeriodList;
    }

    /**
     * @param measurementPeriodList the measurementPeriodList to set
     */
    public void setMeasurementPeriodList(WebElement measurementPeriodList) {
        this.measurementPeriodList = measurementPeriodList;
    }

    /**
     * @return the measurementPeriod
     */
    public WebElement getMeasurementPeriod() {
        return measurementPeriod;
    }

    /**
     * @param measurementPeriod the measurementPeriod to set
     */
    public void setMeasurementPeriod(WebElement measurementPeriod) {
        this.measurementPeriod = measurementPeriod;
    }

    /**
     * @return the saveMq
     */
    public WebElement getSaveMq() {
        return saveMq;
    }

    /**
     * @param saveMq the saveMq to set
     */
    public void setSaveMq(WebElement saveMq) {
        this.saveMq = saveMq;
    }

    /**
     * @return the mQToSearch
     */
    public WebElement getmQToSearch() {
        return mQToSearch;
    }

    /**
     * @param mQToSearch the mQToSearch to set
     */
    public void setmQToSearch(WebElement mQToSearch) {
        this.mQToSearch = mQToSearch;
    }

    /**
     * @return the buttonSearch
     */
    public WebElement getButtonSearch() {
        return buttonSearch;
    }

    /**
     * @param buttonSearch the buttonSearch to set
     */
    public void setButtonSearch(WebElement buttonSearch) {
        this.buttonSearch = buttonSearch;
    }

    /**
     * @return the deleteBtn
     */
    public WebElement getDeleteBtn() {
        return deleteBtn;
    }

    /**
     * @param deleteBtn the deleteBtn to set
     */
    public void setDeleteBtn(WebElement deleteBtn) {
        this.deleteBtn = deleteBtn;
    }
}
