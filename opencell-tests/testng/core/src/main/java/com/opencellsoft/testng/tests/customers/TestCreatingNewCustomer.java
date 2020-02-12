package com.opencellsoft.testng.tests.customers;

import java.util.LinkedHashMap;
import java.util.Map;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.customers.CreateNewCustomerPage;
import com.opencellsoft.testng.tests.base.TestBase;

/**
 * @author miftah
 *
 */
public class TestCreatingNewCustomer extends TestBase {
    
    /** map of infos. */
    public final Map<String, String>  data  = new LinkedHashMap<String, String>();
    
    /**
     * default constructor?
     */
    public TestCreatingNewCustomer() {
        super();
        
        String test = "customer" + System.currentTimeMillis();
        String email = test + "@opencellsoft.com", phone = "0144523300", mobile = "0652623547",
                zipCode = "75011";
        
        data.put(Constants.DESCRIPTION, test);
        data.put(Constants.CODE, test);
        data.put(Constants.FIRST_NAME, test);
        data.put(Constants.LAST_NAME, test);
        data.put(Constants.JOB_TITLE, test);
        data.put(Constants.PHONE, phone);
        data.put(Constants.MOBILE, mobile);
        data.put(Constants.EMAIL, email);
        data.put(Constants.ADDRESS_1, test);
        data.put(Constants.ADDRESS_2, test);
        data.put(Constants.ADDRESS_3, test);
        data.put(Constants.ZIP_CODE, zipCode);
        data.put(Constants.CITY, test);
        data.put(Constants.VAT_NO, test);
        data.put(Constants.REG_NO, test);
        
    }
    
    /**
     * @throws InterruptedException
     * 
     */
    @Test
    public void openCreateCustomer() throws InterruptedException {
        
        CreateNewCustomerPage newCustomerPage = PageFactory.initElements(this.getDriver(),
            CreateNewCustomerPage.class);
        newCustomerPage.openCustomersList(driver);
        
        newCustomerPage.fillCrmHierarchy(driver, data);
        
    }
}
