package com.opencellsoft.testng.tests.marketingmanager;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.marketingmanager.CustomersPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestCustomers extends TestBase {
    @Test
    public void testCutomsersPage() throws InterruptedException {
        
        CustomersPage customersPage = PageFactory.initElements(this.getDriver(), CustomersPage.class);
        Thread.sleep(8000);
        customersPage.customersMenu(driver);
        Thread.sleep(8000);
        customersPage.globalSearch(driver);
        Thread.sleep(8000);
        customersPage.customersMenu(driver);
        Thread.sleep(8000);
        customersPage.customerAccount(driver);
        Thread.sleep(8000);
        customersPage.customersMenu(driver);
        Thread.sleep(8000);
        customersPage.billingAccount(driver);
        Thread.sleep(8000);
        customersPage.customersMenu(driver);
        Thread.sleep(8000);
        customersPage.userAccount(driver);
     
    }
}