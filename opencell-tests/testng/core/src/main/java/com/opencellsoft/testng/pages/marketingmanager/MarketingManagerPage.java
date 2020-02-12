package com.opencellsoft.testng.pages.marketingmanager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;

public class MarketingManagerPage extends BasePage {
    
    public MarketingManagerPage(WebDriver driver) {
        super(driver);
    }
    
    @FindBy(xpath = "/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[1]/a/span[1]")
    private WebElement offersCatalog;
    
    @FindBy(xpath = "/html/body/div[2]/div[1]/div/div/div[4]/ul/li[1]/a")
    private WebElement detailedView;
    
    @FindBy(xpath = "/html/body/div[2]/div[1]/div/div/div[4]/ul/li[2]/a")
    private WebElement condensedView;
    
    @FindBy(id = "tabView:mainForm:dataGrid:0:editLink")
    private WebElement editBtn;
    
    @FindBy(xpath = "/html/body/div[2]/div[1]/div/div/div[1]/div/table/tbody/tr/td[1]/div/div/form/ul/li/div/a")
    private WebElement createNewOffer;
    
    public void gotoOffersPage(WebDriver driver) {
        WebElement element = driver.findElement(By.linkText("Offers"));
        element.click();
        /*
         * WebElement marketingManagePage =
         * driver.findElement(By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[1]/a"));
         * moveMouse(marketingManagePage);
         */
    }
    
    public void gotoProductsPage(WebDriver driver) {
        
        WebElement element1 = driver.findElement(By.linkText("Products"));
        element1.click();
        /*
         * WebElement marketingManagePage = driver
         * .findElement(By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[2]/a"));
         * moveMouse(marketingManagePage);
         */
    }
    
    public void checkOffers(WebDriver driver) throws InterruptedException {
        forceClick(condensedView);
        Thread.sleep(2000);
        forceClick(detailedView);
        Thread.sleep(2000);
        forceClick(editBtn);
        Thread.sleep(2000);
        
    }
    
    public void checkProducts(WebDriver driver) throws InterruptedException {
        
        
        WebElement condensedViewProducts = driver
            .findElement(By.xpath("/html/body/div[2]/div/div/div/div[4]/ul/li[2]/a"));
        forceClick(condensedViewProducts);
        Thread.sleep(2000);
        WebElement detailedViewProducts = driver
                .findElement(By.xpath("/html/body/div[2]/div/div/div/div[4]/ul/li[1]/a"));
            forceClick(detailedViewProducts);
            Thread.sleep(2000);
        WebElement editBtnProducts = driver.findElement(By.xpath(
            "/html/body/div[2]/div/div/div/div[4]/div/div[2]/div/div/form/div[1]/div[2]/table/tbody/tr/td[6]/ul/li[2]/a"));
        forceClick(editBtnProducts);
        Thread.sleep(2000);
        WebElement createNewProduct = driver.findElement(By.xpath(
            "/html/body/div[2]/div/div/div/div[1]/div/table/tbody/tr/td[1]/div/div/form/ul/li[1]/div/a"));
        forceClick(createNewProduct);
        
    }
}
