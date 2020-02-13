package com.opencellsoft.testng.tests.configuration;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.configuration.ChannelsPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Hassnaa MIFTAH.
 *
 **/
public class TestChannels extends TestBase {
    
    public void testChannelsPage() {
        String test = "RE_" + System.currentTimeMillis();
        
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
    }
    
    /**
     * createChannel.
     * 
     * @throws InterruptedException
     */
    @Test
    public void createChannel() throws InterruptedException {
        
        /**
         * init test
         */
        ChannelsPage channelsPage = PageFactory.initElements(this.getDriver(), ChannelsPage.class);
        
        /**
         * Go to Channels page.
         */
        channelsPage.gotoListPage(driver);
        
        /**
         * Go to New Channel Creation page.
         */
        channelsPage.gotoNewPage(driver);
        
        /**
         * Fill the new channel form.
         */
        channelsPage.fillForm(driver, data);
        
        /**
         * Check mandatory fields.
         */
        
    }
    
    /**
     * test mandatory field.
     */
    private void testData() {
        ChannelsPage newEntity = PageFactory.initElements(this.getDriver(), ChannelsPage.class);
        
        String code = newEntity.getCodeCp().getAttribute(ATTRIBUTE_VALUE);
        String description = newEntity.getDescriptionCp().getAttribute(ATTRIBUTE_VALUE);
        
        if (data.get(Constants.CODE) != null) {
            assertEquals(code, data.get(Constants.CODE));
        }
        if (data.get(Constants.DESCRIPTION) != null) {
            assertEquals(description, data.get(Constants.DESCRIPTION));
        }
        
    }
    
}
