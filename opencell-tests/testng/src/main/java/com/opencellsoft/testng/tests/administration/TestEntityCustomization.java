package com.opencellsoft.testng.tests.administration;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.administration.EntityCustomizationPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * entity customization test.
 * 
 * @author MIFTAH
 *
 */
public class TestEntityCustomization extends TestBase {
    /**
     * generate values.
     */
    public TestEntityCustomization() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str = "EC_" + dataKey;
        
        data.put(Constants.CODE, str);
        data.put(Constants.DESCRIPTION, str);
        
    }
    
    /**
     * Offer Model.
     */
    @Test
    public void create() {
        // Page initialisation
        
        EntityCustomizationPage entitycustomizationPage = PageFactory.initElements(this.getDriver(),
            EntityCustomizationPage.class);
        
        // Open offer model Page
        entitycustomizationPage.gotoListPage(driver);
        
        // Entering new offer model
        entitycustomizationPage.goTobtnNew(driver);
        
        // Entering data
        entitycustomizationPage.fillData(driver, data);
       
        // Test Mandatory fields
        testData(entitycustomizationPage);
        
        // Saving data
        entitycustomizationPage.goToSave(driver);
        
        // deleting data
        entitycustomizationPage.delete(driver);
    }
    
    /**
     * Check the mandatory fields.
     * 
     * @param page instance of module .
     */
    private void testData(EntityCustomizationPage entitycustomizationPage) {
        String code = entitycustomizationPage.getcodeEntity().getAttribute(ATTRIBUTE_VALUE);
        String description = entitycustomizationPage.getDescriptionEntity()
            .getAttribute(ATTRIBUTE_VALUE);
        
        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));
        
    }
    
}
