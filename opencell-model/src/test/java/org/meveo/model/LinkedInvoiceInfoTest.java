package org.meveo.model;

import org.junit.Test;
import org.meveo.model.billing.LinkedInvoiceInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
