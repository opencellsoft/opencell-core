package com.opencellsoft.testng.pages.marketingmanager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.opencellsoft.testng.pages.BasePage;

/**
 * @author HASSNAA MIFTAH.
 *
 */
public class SubscriptionPage extends BasePage {
    
    /**
     * Constructor
     * 
     * @param driver WebDriver
     */
    public SubscriptionPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * go to Customers -> Subscription .
     * 
     * @param driver WebDriver
     */
    public void gotoSubscriptionPage(WebDriver driver) {
        /**
         * customers Menu.
         */
        WebElement customersMenu = driver.findElement(
            By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[3]/a/span[1]"));
        forceClick(customersMenu);
        /**
         * subscription Menu.
         */
        WebElement subscriptionMenu = driver.findElement(
            By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[3]/ul/li[7]/a/span"));
        forceClick(subscriptionMenu);
        
    }
    
    /**
     * method to show subsription .
     * 
     * @param driver WebDriver
     * @throws InterruptedException Exception
     */
    public void showSubscription(WebDriver driver) throws InterruptedException {
        /**
        * select a subscirption from the dataTable . 
        */
        WebElement subscriptionDataTableElement = driver
            .findElement(By.id("datatable_results:0:code_id_message_link"));
        forceClick(subscriptionDataTableElement);
        Thread.sleep(2000);
        /**
         * click on subscription Menu.
         */
        WebElement subscriptionBarMenu = driver
            .findElement(By.xpath("/html/body/div[2]/span/span[2]/div/ul/li[2]/a"));
        forceClick(subscriptionBarMenu);
        Thread.sleep(2000);
        /**
         * one Shot Menu.
         */
        WebElement oneShotBarMenu = driver
            .findElement(By.xpath("/html/body/div[2]/span/span[2]/div/ul/li[3]/a"));
        forceClick(oneShotBarMenu);
        Thread.sleep(2000);
        /**
         * reccuring Charge Menu.
         */
        WebElement reccuringChargeMenu = driver
            .findElement(By.xpath("/html/body/div[2]/span/span[2]/div/ul/li[4]/a"));
        forceClick(reccuringChargeMenu);
        Thread.sleep(2000);
        /**
         * usage Charge Menu.
         */
        WebElement usageChargeMenu = driver
            .findElement(By.xpath("/html/body/div[2]/span/span[2]/div/ul/li[5]/a"));
        forceClick(usageChargeMenu);
        Thread.sleep(2000);
        /**
         * product Instance Menu.
         */
        WebElement productInstanceMenu = driver
            .findElement(By.xpath("/html/body/div[2]/span/span[2]/div/ul/li[6]/a"));
        forceClick(productInstanceMenu);
        Thread.sleep(2000);
        /**
         * access Point Menu.
         */
        WebElement accessPointMenu = driver
            .findElement(By.xpath("/html/body/div[2]/span/span[2]/div/ul/li[7]/a"));
        forceClick(accessPointMenu);
        
    }
}
