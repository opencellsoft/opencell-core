package org.meveo.api.invoice;

import static org.junit.rules.ExpectedException.none;
import static org.meveo.model.billing.InvoiceStatusEnum.VALIDATED;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.billing.InvoiceType;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.BadRequestException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceApiTest {

    @Rule
    public final ExpectedException exception = none();

    @InjectMocks
    private InvoiceApi invoiceApi;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private InvoiceTypeService invoiceTypeService;

    @Mock
    private ServiceSingleton serviceSingleton;

    @Mock
    private AccountOperationService accountOperationService;
    @Mock
    private MatchingCodeService matchingCodeService;

    private Long invoiceId = 10000L;
    private BigDecimal rate = new BigDecimal(1.12);
    private Date rateDate = new Date();

    @Test
    public void shouldRefreshAndValidateInvoice() throws ImportInvoiceException, InvoiceExistException, IOException {
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
        invoice.setInvoiceType(new InvoiceType());

        when(serviceSingleton.validateAndAssignInvoiceNumber(invoiceId, true)).thenReturn(invoice);
        when(invoiceService.refreshOrRetrieve(Mockito.any(Invoice.class))).thenReturn(invoice);
        when(invoiceService.findById(invoiceId)).thenReturn(invoice);

        try {
            String invNumber = invoiceApi.validateInvoice(invoiceId, false, true, true);
            Assert.assertEquals("INV_NUMB1", invNumber);
        } catch (Exception e) {
            Assert.fail("Error during validate invoice : " + e.getMessage());
        }
    }


    @Test
    public void validateWithAutoMatchingNominal() {
        // Adj data : Invoice, Type, AO
        InvoiceType typeAdj = buildInvoiceType("ADJ");
        Invoice adjInv = buildInvoice(1L, typeAdj);
        adjInv.setAutoMatching(true);

        when(serviceSingleton.validateAndAssignInvoiceNumber(adjInv.getId(), true)).thenReturn(adjInv);
        when(invoiceService.refreshOrRetrieve(Mockito.any(Invoice.class))).thenReturn(adjInv);
        when(invoiceService.findById(adjInv.getId())).thenReturn(adjInv);
        Mockito.doNothing().when(invoiceService).autoMatchingAdjInvoice(adjInv, null);

        try {
            String invNumber = invoiceApi.validateInvoice(adjInv.getId(), false, true, true);
            Assert.assertEquals("INV_NUMB1", invNumber);
        } catch (Exception e) {
            Assert.fail("Error during validate adjInv : " + e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfInvoiceAlreadyValidated() throws ImportInvoiceException, InvoiceExistException, IOException {
        Invoice invoice = buildInvoice(1L, null);
        invoice.setStatus(VALIDATED);

        when(invoiceService.findById(1L)).thenReturn(invoice);

        exception.expect(BadRequestException.class);
        exception.expectMessage("Invoice already validated");

        invoiceApi.validateInvoice(invoice.getId(), false, true, true);
    }

    private Invoice buildInvoice(long id, InvoiceType typeAdj) {
        Invoice adjInv = new Invoice();
        adjInv.setId(id);
        adjInv.setInvoiceNumber("INV_NUMB1");
        adjInv.setStatus(InvoiceStatusEnum.NEW);
        adjInv.setLastAppliedRate(new BigDecimal("1.12"));
        adjInv.setLastAppliedRateDate(new Date());
        adjInv.setDueDate(new Date());
        adjInv.setAmountTax(BigDecimal.TEN);
        adjInv.setAmountWithTax(new BigDecimal(20));
        adjInv.setAmountWithoutTax(BigDecimal.TEN);
        adjInv.setInvoiceType(typeAdj);
        return adjInv;
    }

    private InvoiceType buildInvoiceType(String ADJ) {
        InvoiceType typeAdj = new InvoiceType();
        typeAdj.setCode(ADJ);
        return typeAdj;
    }

}