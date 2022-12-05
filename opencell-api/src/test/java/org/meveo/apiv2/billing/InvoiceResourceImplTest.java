package org.meveo.apiv2.billing;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.billing.impl.InvoiceResourceImpl;
import org.meveo.apiv2.billing.service.InvoiceApiService;
import org.meveo.model.billing.Invoice;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import jakarta.ws.rs.core.Response;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceResourceImplTest {

    @InjectMocks
    private InvoiceResourceImpl invoiceResource;

    @Mock
    private InvoiceApiService invoiceApiService;

    private Long invoiceId = 1L;

    @Test
    public void testRefreshRateSuccessResponse() {
        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        when(invoiceApiService.refreshRate(invoiceId)).thenReturn(of(invoice));

        Response response = invoiceResource.refreshRate(invoiceId);
        Map<String, Object> responseEntity = (Map<String, Object>) response.getEntity();

        assertEquals(200, response.getStatus());
        assertEquals("Exchange rate successfully refreshed", responseEntity.get("message"));
    }
    @Test
    public void testResponseForRefreshingRateWithSameRateInTradingCurrency() {
        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        when(invoiceApiService.refreshRate(invoiceId)).thenReturn(empty());

        Response response = invoiceResource.refreshRate(invoiceId);
        Map<String, Object> responseEntity = (Map<String, Object>) response.getEntity();

        assertEquals(200, response.getStatus());
        assertEquals("Last applied rate and trading currency current rate are equals",
                responseEntity.get("message"));
    }
}