package com.opencellsoft.testng.tests.configuration;

import static org.testng.Assert.assertEquals;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.configuration.CalendarDetailPage;

import com.opencellsoft.testng.tests.base.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

/**
 * @author Maria AIT BRAHIM
 */
public class TestCalendar extends TestBase {

    /**
     * fill the constants.
     */

    public TestCalendar() {
        String test = "RE_" + System.currentTimeMillis();

        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);

    }

    /**
     * Calendar test.
     * @throws InterruptedException 
     */
    @Test
    private void testCalendar() throws InterruptedException {

        CalendarDetailPage calendardetailPage = PageFactory.initElements(this.getDriver(),
            CalendarDetailPage.class);

        calendardetailPage.gotoListPage(driver);
        calendardetailPage.gotoNewPage(driver);
        calendardetailPage.fillFormCreate(driver, data);
        testData(calendardetailPage);
        calendardetailPage.saveCalendar(driver);
        calendardetailPage.fillFormAndSearch(driver, data);
        calendardetailPage.deleteCalendar(driver);
    }

    /**
     * Check the mandatory fields.
     * 
     * @param newEntity instance of CalendarDetailPage
     */

    private void testData(CalendarDetailPage newEntity) {

        String code = newEntity.getCodeCdp().getAttribute(ATTRIBUTE_VALUE);
        String description = newEntity.getDescriptionCdp().getAttribute(ATTRIBUTE_VALUE);

        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));

    }

}