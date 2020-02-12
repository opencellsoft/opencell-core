package com.opencellsoft.testng.pages.offers;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * offer.
 * 
 * @author AIT BRAHIM Maria
 *
 */
public class Offers extends BasePage {
  public Offers(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

/**
   * button create.
   */
  @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div/a/span[1]/span")
  private WebElement btnCreate;
  /**
   * new code label.
   */
  @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div/div[1]/div/input")
  private WebElement codeOffer;
  /**
   * new description label.
   */
  @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div/div[3]/div/input")
  private WebElement descriptionOffer;
  /**
   * offer code.
   */
  @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div/div[2]/div/input")
  private WebElement name;

  
  
  

  /**
   * Opening offer model menu.
   * 
   * @param driver WebDriver
   */
  public void gotoListPage(WebDriver driver) {
    WebElement offerMenu = driver.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/a[2]"));
    moveMouseAndClick(offerMenu);
    
  }

  /**
   * entering data.
   * 
   * @param driver WebDriver
   * @param data code, description, code, entity
 * @throws InterruptedException 
   */
  public void fillData(WebDriver driver, Map<String, String> data) throws InterruptedException {
     
      btnCreate.click();
      
    codeOffer.click();
    codeOffer.clear();
    codeOffer.sendKeys((String) data.get(Constants.CODE));
    
    descriptionOffer.click();
    descriptionOffer.clear();
    descriptionOffer.sendKeys((String) data.get(Constants.DESCRIPTION));
    
    name.click();
    name.clear();
    name.sendKeys((String) data.get(Constants.DESCRIPTION));
    WebElement detailsMenu = driver.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[2]/span[1]/span/span"));
    
    moveMouseAndClick(detailsMenu);
    WebElement lifecycleStatusList = driver.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[2]/div/div/div[1]/div/div/div"));
    
    moveMouseAndClick(lifecycleStatusList); 
    WebElement lifecycleStatus = driver.findElement(By.xpath("/html/body/div[3]/div[2]/ul/li[4]"));
    
    moveMouseAndClick(lifecycleStatus); 
    
WebElement categoryList = driver.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[2]/div/div/div[2]/div/div/div"));
    
    moveMouseAndClick(categoryList); 
    WebElement category = driver.findElement(By.xpath("/html/body/div[3]/div[2]/ul/li[2]"));
    
    moveMouseAndClick(category); 

    
    
    WebElement btnSave = driver.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button/span[1]"));
    
    moveMouseAndClick(btnSave);
  }

public WebElement getCodeOffer() {
    return codeOffer;
}

public void setCodeOffer(WebElement codeOffer) {
    this.codeOffer = codeOffer;
}

public WebElement getDescriptionOffer() {
    return descriptionOffer;
}

public void setDescriptionOffer(WebElement descriptionOffer) {
    this.descriptionOffer = descriptionOffer;
}

public WebElement getName() {
    return name;
}

public void setName(WebElement name) {
    this.name = name;
}



 

}
