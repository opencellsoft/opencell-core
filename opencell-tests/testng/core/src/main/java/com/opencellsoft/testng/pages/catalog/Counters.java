package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * counters page.
 * 
 * @author Hassnaa MIFTAH
 *
 */
public class Counters extends BasePage {

  /**
   * new code label.
   */
  @FindBy(id = "counterTemplatId:code_txt")
  private WebElement codeCounters;


  /**
   * button delete.
   */
  @FindBy(id = "datatable_results:0:resultsdeletelink")
  private WebElement btnDelete;


  /**
   * constructor.
   * 
   * @param driver WebDriver
   */
  public Counters(final WebDriver driver) {
    super(driver);
    // TODO Auto-generated constructor stub
  }

  /**
   * Opening counters menu.
   * 
   * @param driver WebDriver
   */
  public void gotoListPage(WebDriver driver) {
      WebElement catalogMenu = driver.findElement(By.id("menu:catalog"));
      moveMouse(catalogMenu);
      WebElement serviceManagement = driver.findElement(By.id("menu:serviceManagement"));
      moveMouse(serviceManagement);
      for (int i = 0; i < 2; i++) {
          try
          {
              WebElement counterMenu = driver
                  .findElement(By.id("menu:counterTemplates"));
              moveMouseAndClick(counterMenu);
              break;
          }
          
          catch (StaleElementReferenceException see)
          
          {
          }
      }
  }

  /**
   * entering data.
   * 
   * @param driver WebDriver
   * @param data code, description, choosing 
   */
  public void fillData(WebDriver driver, Map<String, String> data) {
      
    WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
    moveMouseAndClick(btnNew);
    WebElement codeCounters = driver.findElement(By.id("counterTemplatId:code_txt"));
    moveMouseAndClick(codeCounters);
    codeCounters.clear();
    codeCounters.sendKeys((String) data.get(Constants.CODE));
    WebElement descriptionCounters = driver.findElement(By.id("counterTemplatId:description"));
    moveMouseAndClick(descriptionCounters);
    descriptionCounters.clear();
    descriptionCounters.sendKeys((String) data.get(Constants.DESCRIPTION));
    WebElement calendar = driver.findElement(By.id("counterTemplatId:calendar_entity_label"));
    moveMouseAndClick(calendar);
    WebElement selectCalendar = driver.findElement(By.id("counterTemplatId:calendar_entity_1"));
    moveMouseAndClick(selectCalendar);
    WebElement btnSave = driver.findElement(By.id("counterTemplatId:formButtonsCC:saveButton"));
    moveMouseAndClick(btnSave);
    WebElement codeCountersSearch = driver.findElement(By.id("searchForm:code_txt"));
    moveMouseAndClick(codeCountersSearch);
    codeCountersSearch.clear();
    codeCountersSearch.sendKeys((String) data.get(Constants.CODE));
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
    WebElement deletebttn = driver.findElement(By.id("counterTemplatId:formButtonsCC:deletelink"));
    moveMouseAndClick(deletebttn);

    WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
    moveMouseAndClick(yes);
  }

}
