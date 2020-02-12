package com.opencellsoft.testng.tests.entities;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.entities.TicketPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestTicket extends TestBase {
    /**
     * generate values.
     */
    public TestTicket() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "TI_" + dataKey;
        data.put(Constants.CODE, str);
        
    }
    
    @Test
    /**
     * test Ticket page .
     * 
     * @throws InterruptedException Exception
     */
    public void testTicket() throws InterruptedException {
        
        TicketPage ticket = PageFactory.initElements(this.getDriver(), TicketPage.class);
        ticket.openTicketList();
        ticket.createNewEvent(driver, data);
        ticket.searchEventDelete(driver, data);
    }
}
