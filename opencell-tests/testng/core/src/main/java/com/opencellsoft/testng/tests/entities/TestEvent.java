package com.opencellsoft.testng.tests.entities;

import static org.testng.Assert.assertEquals;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.entities.EventPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestEvent extends TestBase {
    /**
     * generate values.
     */
    public TestEvent() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "EV_" + dataKey;
        data.put(Constants.CODE, str);
        
    }
    @Test
    /**
     * test Charts page .
     * 
     * @throws InterruptedException Exception
     */
    public void testEvent() throws InterruptedException {
        
        EventPage event = PageFactory.initElements(this.getDriver(), EventPage.class);
        event.openEventList(driver);
        event.createNewEvent(driver, data);
        event.searchEventDelete(driver, data);
    }
    /**
     * Check the mandatory fields.
     * 
     * @param page instance of Filter .
     */
    private void testData(EventPage page) {
      String code = page.getCode().getAttribute(ATTRIBUTE_VALUE);

      assertEquals(code, data.get(Constants.CODE));

    }
}
