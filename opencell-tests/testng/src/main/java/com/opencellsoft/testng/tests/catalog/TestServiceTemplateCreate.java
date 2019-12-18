package com.opencellsoft.testng.tests.catalog;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.ServiceTemplateDetailPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author Edward P. Legaspi
 * 
 */
public class TestServiceTemplateCreate extends TestBase {

    /**
     * default constructor.
     */
    public TestServiceTemplateCreate() {
        String test = "ST_" + System.currentTimeMillis();
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
        data.put(Constants.LONG_DESCRIPTION, test);
    }

    /**
     * test create service template.
     * @throws InterruptedException 
     */
    @Test
    private void testCreate() throws InterruptedException {
        ServiceTemplateDetailPage detailPage = PageFactory.initElements(this.getDriver(), ServiceTemplateDetailPage.class);
        detailPage.gotoListPage(driver);
        detailPage.fillFormAndSave(driver, data);
    }

    /**
     * test data.
     */
    private void testData() {
        ServiceTemplateDetailPage newEntity = PageFactory.initElements(this.getDriver(), ServiceTemplateDetailPage.class);

        String code = newEntity.codeIpt.getAttribute(ATTRIBUTE_VALUE);
        String description = newEntity.descriptionIpt.getAttribute(ATTRIBUTE_VALUE);
        String longDescription = newEntity.longDescriptionIpt.getAttribute(ATTRIBUTE_VALUE);

        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));
        assertEquals(longDescription, data.get(Constants.LONG_DESCRIPTION));
    }

}
