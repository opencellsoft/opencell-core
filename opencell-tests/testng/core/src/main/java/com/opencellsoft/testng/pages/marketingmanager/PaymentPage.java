package com.opencellsoft.testng.pages.marketingmanager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.opencellsoft.testng.pages.BasePage;

/**
 * @author HASSNAA MIFTAH
 *
 */
public class PaymentPage extends BasePage {
    
    /**
     * Constructor .
     * 
     * @param driver
     */
    public PaymentPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * go to Payments Page.
     * 
     * @param driver WebDriver
     * @throws InterruptedException Exception
     */
    public void gotoPaymentPage(WebDriver driver) throws InterruptedException {
        /**
         * go to payment Menu.
         */
        WebElement paymentMenu = driver.findElement(
            By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[7]/a/span[1]"));
        forceClick(paymentMenu);
        Thread.sleep(2000);
        
    }
    
    public void paymentGateWay(WebDriver driver) throws InterruptedException {
        /**
         * payment GateWay Menu.
         */
        WebElement paymentGateWayMenu = driver.findElement(
            By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[7]/ul/li[1]/a/span"));
        forceClick(paymentGateWayMenu);
        Thread.sleep(2000);
        /**
         * payment Gate Way element to show.
         */
        WebElement paymentGateWay1 = driver
            .findElement(By.id("datatable_results:0:code_id_message_link"));
        forceClick(paymentGateWay1);
        Thread.sleep(2000);
        /**
         * payment GateWay Menu element to show.
         */
        WebElement backBtn1 = driver
            .findElement(By.id("formPaymentGateway:formButtonsCC:backButton"));
        forceClick(backBtn1);
        Thread.sleep(2000);
        /**
         * payment GateWay Menu element to show.
         */
        WebElement paymentGateWay2 = driver
            .findElement(By.id("datatable_results:1:code_id_message_link"));
        forceClick(paymentGateWay2);
        Thread.sleep(2000);
        /**
         * back button .
         */
        WebElement backBtn2 = driver
            .findElement(By.id("formPaymentGateway:formButtonsCC:backButton"));
        forceClick(backBtn2);
        Thread.sleep(2000);
        /**
         * DD Request State Menu to show .
         */
        WebElement paymentGateWay3 = driver
            .findElement(By.id("datatable_results:2:code_id_message_link"));
        forceClick(paymentGateWay3);
        Thread.sleep(2000);
        /**
         * Back button.
         */
        WebElement backBtn3 = driver
            .findElement(By.id("formPaymentGateway:formButtonsCC:backButton"));
        forceClick(backBtn3);
        Thread.sleep(2000);
        /**
         * DD Request Lot Menu to show .
         */
        WebElement paymentGateWay4 = driver
            .findElement(By.id("datatable_results:3:code_id_message_link"));
        forceClick(paymentGateWay4);
    }
    
    /**
     * go to DD Request State.
     * 
     * @param driver WebDriver
     * @throws InterruptedException Exception
     */
    public void ddRequestState(WebDriver driver) throws InterruptedException {
        /**
         * DD Request State Menu.
         */
        WebElement ddRequestStateMenu = driver.findElement(
            By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[7]/ul/li[2]/a/span"));
        forceClick(ddRequestStateMenu);
        Thread.sleep(2000);
        /**
         * show ddRequestState from the dataTable .
         */
        WebElement ddRequestState1 = driver.findElement(
            By.xpath("/html/body/div[2]/div/div[1]/form/div[1]/div[2]/table/tbody/tr/td[2]"));
        forceClick(ddRequestState1);
        /**
         * Back Button
         */
        WebElement editBtn = driver.findElement(By.id("datatable_results:0:resultseditlink"));
        forceClick(editBtn);
        
    }
    
    /**
     * go to DD Request Lot.
     * 
     * @param driver WebDriver
     * @throws InterruptedException Exception
     */
    public void ddRequestLot(WebDriver driver) throws InterruptedException {
        
        /**
         * DD Request Lot Menu.
         */
        WebElement ddRequestLotMenu = driver.findElement(
            By.xpath("/html/body/div[1]/div[2]/form/div/div[2]/div/ul/li[7]/ul/li[3]/a/span"));
        forceClick(ddRequestLotMenu);
        
    }
}
