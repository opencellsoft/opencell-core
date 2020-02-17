package com.opencellsoft.testng.tests.reporting;

import static org.testng.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.reporting.ChartsPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * 
 * @author Miftah
 *
 */

public class TestCharts extends TestBase {
    protected final Map<String, String> data1 = new LinkedHashMap<>();
    protected final Map<String, String> data2 = new LinkedHashMap<>();
    protected final Map<String, String> data3 = new LinkedHashMap<>();
    
    /**
     * generate values.
     */
    public TestCharts() {
        dataKey = String.valueOf(System.currentTimeMillis());
        String str1 = "BC_" + dataKey;
        String str2 = "PC_" + dataKey;
        String str3 = "LC_" + dataKey;
        data1.put(Constants.CODE, str1);
        data2.put(Constants.CODE, str2);
        data3.put(Constants.CODE, str3);
    }
    
    /**
     * Charts Model.
     */
    @Test
    /**
     * test Charts page .
     * 
     * @throws InterruptedException Exception
     */
    public void testCharts() throws InterruptedException {
        
        ChartsPage charts = PageFactory.initElements(this.getDriver(), ChartsPage.class);
        charts.openChartsList();
        charts.createNewBarChart(driver, data1);
        charts.searchBarChartDelete(driver, data1);
        
        charts.createNewPieChart(driver, data2);
        charts.searchBarChartDelete(driver, data2);
        charts.createNewLineChart(driver, data3);
        charts.searchBarChartDelete(driver, data3);
        
    }
    /**
     * Check the mandatory fields.
     * 
     * @param page instance of Chart .
     */
    /*
     * private void testData(ChartsPage page) { String code =
     * page.getCodeChart().getAttribute(ATTRIBUTE_VALUE);
     * 
     * assertEquals(code, data.get(Constants.CODE));
     * 
     * }
     */
    
}
