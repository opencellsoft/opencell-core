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
public class ChartsPage extends BasePage {
    
    /**
     * menuReporting Menu.
     */
    @FindBy(id = "menu:reporting")
    private WebElement menuReporting;
    /**
     * menu charts Page.
     */
    @FindBy(id = "menu:charts")
    private WebElement menuCharts;
    
    /**
     * buttonNew.
     */
    @FindBy(id = "buttonNewBar")
    private WebElement buttonNewBar;
    
    /**
     * buttonNewLine.
     */
    @FindBy(id = "buttonNewLine")
    private WebElement buttonNewLine;
    /**
     * codeChart.
     */
    @FindBy(id = "formChat:code_txt")
    private WebElement codeChart;
    
    /**
     * Measurable Quantity List.
     */
    @FindBy(id = "formChat:measurableQuantityId_selectLink")
    private WebElement measurableQuantityList;
    
    /**
     * Measurable Quantity Value.
     */
    @FindBy(xpath = "/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[2]/td[1]")
    private WebElement measurableQuantityValue;
    
    /**
     * Bouton save.
     */
    @FindBy(id = "formChat:formButtonsCC:saveButton")
    private WebElement buttonSave;
    
    /**
     * Measurable Quantity List.
     */
    @FindBy(id = "formChat:measurableQuantityId_selectLink")
    private WebElement measurableQuantityListPie;
    
    /**
     * Measurable Quantity Value.
     */
    @FindBy(xpath = "/html/body/div[8]/div[2]/form/div[2]/div[2]/table/tbody/tr[3]/td[1]")
    private WebElement measurableQuantityValuePie;
    
    /**
     * Bouton save.
     */
    @FindBy(id = "formChat:formButtonsCC:saveButton")
    private WebElement buttonSavePie;
    
    public ChartsPage(WebDriver driver) {
        super(driver);
    }
    
    public void openChartsList() throws InterruptedException {
        moveMouse(menuReporting);
        moveMouseAndClick(menuCharts);
    }
    
   
    
    public void createNewBarChart(final WebDriver driver, Map<String, String> data1)
            throws InterruptedException {
       
        buttonNewBar.click();
       
        moveMouseAndClick(codeChart);
        codeChart.clear();
        codeChart.sendKeys((String) data1.get(Constants.CODE));
        
        forceClick(measurableQuantityList);
        
        moveMouseAndClick(measurableQuantityValue);
        
        forceClick(buttonSave);
    }
    
    public void createNewPieChart(final WebDriver driver, Map<String, String> data2)
            throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            try
            
            {
                WebElement buttonNewPie = driver.findElement(By.id("buttonNewPie"));
                moveMouseAndClick(buttonNewPie);
                break;
            }
            
            catch (StaleElementReferenceException see)
            
            {
            }
        }
        
        WebElement codeChart = driver.findElement(By.id("formChat:code_txt"));
     
        moveMouseAndClick(codeChart);
        codeChart.clear();
        codeChart.sendKeys((String) data2.get(Constants.CODE));
        
        
 forceClick(measurableQuantityListPie);
        
        moveMouseAndClick(measurableQuantityValuePie);
        
        forceClick(buttonSavePie);
        
       
    }
    
    public void createNewLineChart(final WebDriver driver, Map<String, String> data3)
            throws InterruptedException {
     
        forceClick(buttonNewLine);
        
        WebElement codeChart = driver.findElement(By.id("formChat:code_txt"));
      
        moveMouseAndClick(codeChart);
        codeChart.clear();
        codeChart.sendKeys((String) data3.get(Constants.CODE));
        
        
        
        forceClick(measurableQuantityListPie);
               
               moveMouseAndClick(measurableQuantityValuePie);
               
               forceClick(buttonSavePie);
               
   
    }
    
    public void searchBarChartDelete(final WebDriver driver, Map<String, String> data1)
            throws InterruptedException {
        WebElement codeToDelete = driver.findElement(By.id("searchForm:code_txt"));
      
        moveMouseAndClick(codeToDelete);
        codeToDelete.clear();
        codeToDelete.sendKeys((String) data1.get(Constants.CODE));
        WebElement buttonSearch = driver.findElement(By.id("searchForm:buttonSearch"));
        
        forceClick(buttonSearch);
        
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
        WebElement deleteBtn = driver.findElement(By.id("formChat:formButtonsCC:deletelink"));
       
        forceClick(deleteBtn);
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
     * @return the menuCharts
     */
    public WebElement getMenuCharts() {
        return menuCharts;
    }
    
    /**
     * @param menuCharts the menuCharts to set
     */
    public void setMenuCharts(WebElement menuCharts) {
        this.menuCharts = menuCharts;
    }
    
    /**
     * @return the buttonNewBar
     */
    public WebElement getButtonNewBar() {
        return buttonNewBar;
    }
    
    /**
     * @param buttonNewBar the buttonNewBar to set
     */
    public void setButtonNewBar(WebElement buttonNewBar) {
        this.buttonNewBar = buttonNewBar;
    }
    
    /**
     * @return the buttonNewLine
     */
    public WebElement getButtonNewLine() {
        return buttonNewLine;
    }
    
    /**
     * @param buttonNewLine the buttonNewLine to set
     */
    public void setButtonNewLine(WebElement buttonNewLine) {
        this.buttonNewLine = buttonNewLine;
    }
    
    /**
     * @return the codeChart
     */
    public WebElement getCodeChart() {
        return codeChart;
    }
    
    /**
     * @param codeChart the codeChart to set
     */
    public void setCodeChart(WebElement codeChart) {
        this.codeChart = codeChart;
    }
}
