package org.meveo.api.invoice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceApiTest {

    @InjectMocks
    private InvoiceApi invoiceApi;

    @Mock
    private InvoiceService invoiceService;
    
    
    @Mock
    private InvoiceLineService invoiceLineService;

    @Mock
    private ServiceSingleton serviceSingleton;

    private Long invoiceId = 10000L;
    private BigDecimal rate = new BigDecimal(1.12);
    private Date rateDate = new Date();

    @Test
    public void shouldRefreshAndValidateInvoice() throws ImportInvoiceException, InvoiceExistException ,IOException{
        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setInvoiceNumber("INV_NUMB1");
        invoice.setStatus(InvoiceStatusEnum.NEW);
        invoice.setLastAppliedRate(rate);
        invoice.setLastAppliedRateDate(rateDate);
        invoice.setDueDate(new Date());
        invoice.setAmountTax(BigDecimal.TEN);
        invoice.setAmountWithTax(new BigDecimal(20));
        invoice.setAmountWithoutTax(BigDecimal.TEN);

        Mockito.when(serviceSingleton.validateAndAssignInvoiceNumber(invoiceId, true)).thenReturn(invoice);

        Mockito.when(invoiceService.refreshOrRetrieve(Mockito.any(Invoice.class))).thenReturn(invoice);

        Mockito.when(invoiceService.findById(invoiceId)).thenReturn(invoice);
        
        doNothing().when(invoiceLineService).passInvoiceLinesToBilled(invoice);
        
                
        try {
            String invNumber = invoiceApi.validateInvoice(invoiceId, false, true, true);
            Assert.assertEquals("INV_NUMB1", invNumber);
        } catch (Exception e) {
            Assert.fail("Error during validate invoice : " + e.getMessage());
        }
    }
}