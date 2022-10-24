package org.meveo.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.billing.LinkedInvoiceInfo;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
@RunWith(MockitoJUnitRunner.class)
public class LinkedInvoiceInfoTest {

    @Test
    public void Should_Pass_All_Pojo_Tests() {

        //given
        final LinkedInvoiceInfo linkedInvoiceInfo = new LinkedInvoiceInfo("1", null, null);

        //then
        assertEquals("1", linkedInvoiceInfo.getInvoiceNumber());
        assertNull(linkedInvoiceInfo.getInvoiceDate());
        assertNull(linkedInvoiceInfo.getAmountWithTax());
    }
}
