package com.opencellsoft.testng.tests.quotesandorders;

import static org.testng.Assert.assertEquals;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.quotesandorders.OrdersPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestOrders extends TestBase {
    public TestOrders() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "OR_" + dataKey;
        data.put(Constants.CODE, str);
        
    }
    
    @Test
    public void testOrdersPage() throws InterruptedException {
        // Page initialisation
        OrdersPage ordersPage = PageFactory.initElements(this.getDriver(), OrdersPage.class);
        ordersPage.openOrdersList();
        ordersPage.fillOrders(driver, data);
        ordersPage.saveOrders(driver);
        ordersPage.searchOrdersAndDelete(driver, data);
    }
    
}
