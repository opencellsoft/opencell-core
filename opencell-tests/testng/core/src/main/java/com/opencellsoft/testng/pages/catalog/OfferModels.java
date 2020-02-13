package com.opencellsoft.testng.pages.catalog;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * offer model.
 * 
 * @author Miftah
 *
 */
public class OfferModels extends BasePage {
  /**
   * button new.
   */
  @FindBy(id = "searchForm:buttonNew")
  private WebElement btnNew;
  /**
   * new code label.
   */
  @FindBy(id = "moduleForm:code_txt")
  private WebElement codeOfferModels;
  /**
   * new description label.
   */
  @FindBy(id = "moduleForm:description")
  private WebElement descriptionOfferModel;
  /**
   * offer code.
   */
  @FindBy(id = "moduleForm:offerSelectId_selectLink")
  private WebElement offerCode;

  /**
   * constructor.
   * 
   * @param driver WebDriver
   */
  public OfferModels(final WebDriver driver) {
    super(driver);
  }

  /**
   * Opening offer model menu.
   * 
   * @param driver WebDriver
   */
  public void gotoListPage(WebDriver driver) {
    WebElement catalogMenu = driver.findElement(By.id("menu:catalog"));
    moveMouseAndClick(catalogMenu);
    WebElement modelsMenu = driver.findElement(By.id("menu:models"));
    moveMouseAndClick(modelsMenu);
    WebElement offermodels = driver.findElement(By.id("menu:businessOfferModels"));
    moveMouseAndClick(offermodels);
  }

  /**
   * entering data.
   * 
   * @param driver WebDriver
   * @param data code, description, code, entity
 * @throws InterruptedException 
   */
  public void fillData(WebDriver driver, Map<String, String> data) throws InterruptedException {
      btnNew.click();
    codeOfferModels.click();
    codeOfferModels.clear();
    codeOfferModels.sendKeys((String) data.get(Constants.CODE));
    descriptionOfferModel.click();
    descriptionOfferModel.clear();
    descriptionOfferModel.sendKeys((String) data.get(Constants.DESCRIPTION));
    offerCode.click();
    driver.findElement(By.cssSelector("tr.ui-datatable-even:nth-child(1) > td:nth-child(1)")).click();
    WebElement moduleIandA = driver.findElement(By.id("moduleForm:script_selectLink"));
    moveMouseAndClick(moduleIandA);
    WebElement element = driver.findElement(By.xpath("/html/body/div[10]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]"));
    moveMouseAndClick(element);
    WebElement btnSave = driver.findElements(By.className("ui-button-text-icon-left")).get(0);
    moveMouseAndClick(btnSave);
    moveMouseAndClick(driver.findElements(By.className("ui-button-text-icon-left")).get(1));
    WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
    yes.click();
  }
  /**
   * code setter.
   * 
   * @param codeOfferModels setter
   */
  public void setcodeOfferModels(WebElement codeOfferModels) {
    this.codeOfferModels = codeOfferModels;
  }

  /**
   * code getter.
   * 
   * @return code
   */
  public WebElement getcodeOfferModels() {
    return this.codeOfferModels;
  }

  /**
   * description setter.
   * 
   * @param descriptionOfferModel setter
   */
  public void setdescriptionOfferModel(WebElement descriptionOfferModel) {
    this.descriptionOfferModel = descriptionOfferModel;
  }

  /**
   * description getter.
   * 
   * @return description
   */
  public WebElement getdescriptionOfferModel() {
    return this.descriptionOfferModel;
  }

}
