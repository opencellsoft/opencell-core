package com.opencellsoft.testng.pages.marketingmanager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.opencellsoft.testng.pages.BasePage;

/**
 * @author HASSNAA MIFTAH 
 *
 */
public class InvoicePage extends BasePage {
    
    /**
     * Constructor .
     * 
     * @param driver WebDriver .
     */
    public InvoicePage(WebDriver driver) {
        super(driver);
    }
    
    public void billing(WebDriver driver) throws InterruptedException {
        /**
         * click on billing menu .
         */
        WebElement billingMenu = driver.findElement(
            By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[6]/a/span[1]"));
        forceClick(billingMenu);
        Thread.sleep(2000);

        /**
         * click on invoice Menu.
         */
        WebElement invoiceMenu = driver.findElement(
            By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[6]/ul/li[4]/a/span[1]"));
        forceClick(invoiceMenu);
        Thread.sleep(2000);

        /**
         * click on invoices Menu.
         */
        WebElement invoicesMenu = driver.findElement(By.xpath(
            "/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[6]/ul/li[4]/ul/li[1]/a/span"));
        forceClick(invoicesMenu);
        Thread.sleep(2000);

        /**
         * check invoice .
         */
        WebElement invoice = driver.findElement(
            By.id("datatable_results:0:invoiceNumberOrTemporaryNumber_id_message_link"));
        forceClick(invoice);
        
    }
}
