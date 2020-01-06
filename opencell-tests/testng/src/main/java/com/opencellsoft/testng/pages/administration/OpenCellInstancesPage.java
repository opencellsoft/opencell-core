package com.opencellsoft.testng.pages.administration;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * @author Maria AIT BRAHIM
 *
 */
public class OpenCellInstancesPage extends BasePage  {

  /**
   * code.
   */
  @FindBy(id = "meveoInstanceForm:code_txt")
  private WebElement code;

  /**
   * url.
   */
  @FindBy(id = "meveoInstanceForm:url_txt")
  private WebElement url;

  /**
   * description.
   */
  @FindBy(id = "meveoInstanceForm:description_txt")
  private WebElement description;

  /**
   * usernName.
   */
  @FindBy(id = "meveoInstanceForm:authUsername_txt")
  private WebElement usernName;

  /**
   * button search.
   */
  @FindBy(id = "searchForm:buttonSearch")
  private WebElement searchBttnCtp;

  /**
   * code search.
   */
  @FindBy(id = "searchForm:code_txt")
  private WebElement codeSearch;

  /**
   * selected row for delete.
   */
  @FindBy(id = "datatable_results:0:code_id_message_link")
  private WebElement deleteRow;

  /**
   * button delete.
   */
  @FindBy(id = "meveoInstanceForm:formButtonsCC:deletelink")
  private WebElement deleteBttn;

  /**
   * password.
   */
  @FindBy(id = "meveoInstanceForm:authPassword_txt")
  private WebElement password;

  /**
   * button save.
   */
  @FindBy(id = "meveoInstanceForm:formButtonsCC:saveButton")
  private WebElement bttnSave;

  /**
   * button New.
   */
  @FindBy(id = "searchForm:buttonNew")
  private WebElement bttnNew;

  /**
   * constructor of Instances.
   * 
   * @param driver WebDriver
   */
  public OpenCellInstancesPage(final WebDriver driver) {
    super(driver);

  }

  /**
   * click on administration -> openCell Instances.
   * 
   * @param driver WebDriver
   * 
   */
  public void gotoListPage(final WebDriver driver) {

    WebElement administrationMenu = driver.findElement(By.id("menu:automation"));
    moveMouse(administrationMenu);

    WebElement meveoInstanceslMenu = driver.findElement(By.id("menu:MeveoInstances"));
    moveMouseAndClick(meveoInstanceslMenu);

  }

  /**
   * click on Opencell Instances.
   * 
   * @param driver WebDriver
   */

  public void gotoNewPage(WebDriver driver) {
      moveMouseAndClick(bttnNew);
  }

  /**
   * the button save.
   * 
   * @param driver WebDriver
   */
  public void save(WebDriver driver) {
      moveMouseAndClick(bttnSave);
  }

  /**
   * fill the new instance.
   * 
   * @param driver instance of WebDriver
   * @param data Map
   */

  public void fillFormCreate(WebDriver driver, Map<String, String> data) {
    // Code
    getCode().click();
    getCode().clear();
    getCode().sendKeys((String) data.get(Constants.CODE));

    // userName
    getUsernName().click();
    getUsernName().sendKeys("Opencell");

    // Password
    getPassword().click();
    getPassword().sendKeys("Opencell");

    // url
    getUrl().click();
    getUrl().sendKeys("http://integration.i.opencellsoft.com");

    // Description
    getDescription().click();
    getDescription().clear();
    getDescription().sendKeys((String) data.get(Constants.DESCRIPTION));

  }

  /**
   * search for the new created Instance.
   * 
   * @param driver instance of WebDriver
   * @param data Map
   */
  public void fillFormAndSearch(WebDriver driver, Map<String, String> data) {
    moveMouseAndClick(codeSearch);
    codeSearch.clear();
    codeSearch.sendKeys((String) data.get(Constants.CODE));
    moveMouseAndClick(searchBttnCtp);
  }

  /**
   * delete Instance.
   * 
   * @param driver WebDriver
 * @throws InterruptedException 
   */
  public void delete(WebDriver driver) throws InterruptedException {
    WebElement deleteRow = driver.findElement(By.id("datatable_results:0:code_id_message_link"));
    moveMouseAndClick(deleteRow);
    moveMouseAndClick(deleteBttn);
    WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
    moveMouseAndClick(confirmDelete);
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
   * @return the url
   */
  public final WebElement getUrl() {
    return url;
  }

  /**
   * @param url the url to set
   */
  public final void setUrl(WebElement url) {
    this.url = url;
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

  /**
   * @return the usernName
   */
  public final WebElement getUsernName() {
    return usernName;
  }

  /**
   * @param usernName the usernName to set
   */
  public final void setUsernName(WebElement usernName) {
    this.usernName = usernName;
  }

  /**
   * @return the password
   */
  public final WebElement getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public final void setPassword(WebElement password) {
    this.password = password;
  }

}
