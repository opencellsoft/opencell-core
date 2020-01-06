package com.opencellsoft.testng.tests.finances;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.finances.ReportExtractPage;
import com.opencellsoft.testng.tests.base.TestBase;
/**
 * 
 * @author Miftah
 *
 */

public class TestReportExtract extends TestBase {
        /**
         * generate values.
         */
        public TestReportExtract() {
            dataKey = String.valueOf(System.currentTimeMillis());
            String str = "TC_" + dataKey;
            
            data.put(Constants.CODE, str);
        }
        /**
         * ReportExtract Model.
         */
        @Test
        /**
         * test TestReportExtract page .
         * 
         * @throws InterruptedException Exception
         */
        public void testReportExtract() throws InterruptedException {
            
            ReportExtractPage reportExtract = PageFactory.initElements(this.getDriver(),
                ReportExtractPage.class);
            reportExtract.openReportExtractList(driver);
            reportExtract.fillReportExtractAndSave(driver, data);
            reportExtract.searchReportExtractPageAndDelete(driver, data);
            
        }
}
