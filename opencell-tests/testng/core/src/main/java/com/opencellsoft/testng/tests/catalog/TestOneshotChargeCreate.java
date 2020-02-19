package com.opencellsoft.testng.tests.catalog;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.catalog.ChargeTemplatePage;
import com.opencellsoft.testng.pages.catalog.OneshotChargeDetailPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author Edward P. Legaspi
 * 
 */
public class TestOneshotChargeCreate extends TestBase {

    /**
     * default constructor.
     */
    public TestOneshotChargeCreate() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String test = "OS_" + dataKey;

        data.put(Constants.CODE, test);
        data.put(Constants.INPUT_DESCRIPTION, "HOUR");
        data.put(Constants.RATING_DESCRIPTION, "MINS");
        data.put(Constants.UNIT_MULTIPLICATOR, "60");
        data.put(Constants.UNIT_NB_DECIMAL, "2");
        data.put(Constants.FILTER_EXPRESSION, "true");
        data.put(Constants.UNIT_NB_DECIMAL, "2");
        data.put(Constants.DESCRIPTION, test);
    }

    /**
     * test creation.
     * @throws InterruptedException 
     */
    @Test
    private void testCreate() throws InterruptedException {
        OneshotChargeDetailPage detailPage = PageFactory.initElements(this.getDriver(), OneshotChargeDetailPage.class);
        detailPage.gotoListPage(driver);
        detailPage.fillFormAndSave(driver, data);
       // testData();
    }

    /**
     * test input data if it is well saved.
     */
    private void testData() {
        OneshotChargeDetailPage newEntity = PageFactory.initElements(this.getDriver(), OneshotChargeDetailPage.class);

        String description = newEntity.descriptionIpt.getAttribute(ATTRIBUTE_VALUE);
        String oneShotChargeType = newEntity.getDriver().findElement(By.id("formId:tabView:oneShotChargeTemplateType_enum_input")).getAttribute(ATTRIBUTE_VALUE);
        String ratingDescription = newEntity.ratingDescriptionIpt.getAttribute(ATTRIBUTE_VALUE);
        String unitMultiplicator = newEntity.unitMultiplicatorIpt.getAttribute(ATTRIBUTE_VALUE);
        String unitNbDecimal = newEntity.unitNbDecimalIpt.getAttribute(ATTRIBUTE_VALUE);
        String filterExpression = newEntity.filterExpressionIpt.getAttribute(ATTRIBUTE_VALUE);
        String amountEditable = newEntity.amountEditableIpt.getAttribute(ATTRIBUTE_VALUE);
        String roundingModeUp = newEntity.getDriver().findElement(By.id("formId:tabView:roundingMode_enum_input")).getAttribute(ATTRIBUTE_VALUE);
        String invoiceSubCatConsumption = newEntity.getDriver().findElement(By.id("formId:tabView:invoiceSubCategorySelectedId_input")).getAttribute(ATTRIBUTE_VALUE);

        assertEquals(description, data.get(Constants.DESCRIPTION));
        assertEquals(oneShotChargeType, ChargeTemplatePage.DD_ONESHOT_TYPE_SUBSCRIPTION);
        assertEquals(ratingDescription, data.get(Constants.RATING_DESCRIPTION));
        assertEquals(unitMultiplicator, data.get(Constants.UNIT_MULTIPLICATOR));
        assertEquals(unitNbDecimal, data.get(Constants.UNIT_NB_DECIMAL));
        assertEquals(filterExpression, data.get(Constants.FILTER_EXPRESSION));
        assertEquals(amountEditable, data.get(Constants.AMOUNT_EDITABLE));
        assertEquals(roundingModeUp, ChargeTemplatePage.DD_ROUNDING_MODE_UP);
        assertEquals(invoiceSubCatConsumption, ChargeTemplatePage.DD_INVOICE_SUB_CATEGORY_CONSUMPTION);
    }

}
