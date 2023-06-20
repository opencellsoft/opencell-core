package org.meveo.api.invoice;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.LinkedInvoice;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.MatchingCodeService;
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
        invoice.setInvoiceType(new InvoiceType());

        Mockito.when(serviceSingleton.validateAndAssignInvoiceNumber(invoiceId, true)).thenReturn(invoice);

        Mockito.when(invoiceService.refreshOrRetrieve(Mockito.any(Invoice.class))).thenReturn(invoice);

        Mockito.when(invoiceService.findById(invoiceId)).thenReturn(invoice);

        Mockito.when(invoiceTypeService.getListAdjustementCode()).thenReturn(new ArrayList<>());
                
        try {
            String invNumber = invoiceApi.validateInvoice(invoiceId, false, true, true);
            Assert.assertEquals("INV_NUMB1", invNumber);
        } catch (Exception e) {
            Assert.fail("Error during validate invoice : " + e.getMessage());
        }
    }


    @Test
    public void validateWithAutoMatchingNominal() throws UnbalanceAmountException, NoAllOperationUnmatchedException {
        // Adj data : Invoice, Type, AO
        InvoiceType typeAdj = buildInvoiceType("ADJ");
        Invoice adjInv = buildInvoice(1L, typeAdj);
        adjInv.setAutoMatching(true);
        AccountOperation adjAo = buildAccountOperation(1L);

        // Original invoice  data : Invoice, Type, AO
        InvoiceType typeCom = buildInvoiceType("COM");
        Invoice originalInv = buildInvoice(-1L, typeCom);
        AccountOperation originalAo = buildAccountOperation(2L);

        LinkedInvoice linkedInvoice = new LinkedInvoice();
        linkedInvoice.setInvoice(originalInv);
        linkedInvoice.setLinkedInvoiceValue(adjInv);

        Mockito.when(serviceSingleton.validateAndAssignInvoiceNumber(adjInv.getId(), true)).thenReturn(adjInv);
        Mockito.when(invoiceService.refreshOrRetrieve(Mockito.any(Invoice.class))).thenReturn(adjInv);
        Mockito.when(invoiceService.findById(adjInv.getId())).thenReturn(adjInv);
        Mockito.when(invoiceTypeService.getListAdjustementCode()).thenReturn(List.of("ADJ"));

        Mockito.when(invoiceService.findBySourceInvoiceByAdjId(adjInv.getId())).thenReturn(linkedInvoice);
        Mockito.when(accountOperationService.listByInvoice(linkedInvoice.getInvoice())).thenReturn(List.of(originalAo));
        Mockito.when(accountOperationService.listByInvoice(linkedInvoice.getLinkedInvoiceValue())).thenReturn(List.of(adjAo));

        try {
            String invNumber = invoiceApi.validateInvoice(adjInv.getId(), false, true, true);
            Assert.assertEquals("INV_NUMB1", invNumber);
        } catch (Exception e) {
            Assert.fail("Error during validate adjInv : " + e.getMessage());
        }
    }

    @Test
    public void validateWithAutoMatchingErrPaidInvoice() throws UnbalanceAmountException, NoAllOperationUnmatchedException {
        // Adj data : Invoice, Type, AO
        InvoiceType typeAdj = buildInvoiceType("ADJ");
        Invoice adjInv = buildInvoice(1L, typeAdj);
        adjInv.setAutoMatching(true);
        adjInv.setPaymentStatus(InvoicePaymentStatusEnum.PAID);
        AccountOperation adjAo = buildAccountOperation(1L);

        // Original invoice  data : Invoice, Type, AO
        InvoiceType typeCom = buildInvoiceType("COM");
        Invoice originalInv = buildInvoice(-1L, typeCom);
        AccountOperation originalAo = buildAccountOperation(2L);

        LinkedInvoice linkedInvoice = new LinkedInvoice();
        linkedInvoice.setInvoice(originalInv);
        linkedInvoice.setLinkedInvoiceValue(adjInv);

        Mockito.when(serviceSingleton.validateAndAssignInvoiceNumber(adjInv.getId(), true)).thenReturn(adjInv);
        Mockito.when(invoiceService.refreshOrRetrieve(Mockito.any(Invoice.class))).thenReturn(adjInv);
        Mockito.when(invoiceService.findById(adjInv.getId())).thenReturn(adjInv);
        Mockito.when(invoiceTypeService.getListAdjustementCode()).thenReturn(List.of("ADJ"));

        try {
            invoiceApi.validateInvoice(adjInv.getId(), false, true, true);
            Assert.fail("PAID Invoice exception must be thrown");
        } catch (Exception e) {
            Assert.assertEquals("The Adjustment invoice is already paid, we can not process auto-matching for the linked AccountOperation", e.getMessage());
        }
    }

    @Test
    public void validateWithAutoMatchingErrNoLinkedInvoice() throws UnbalanceAmountException, NoAllOperationUnmatchedException {
        // Adj data : Invoice, Type, AO
        InvoiceType typeAdj = buildInvoiceType("ADJ");
        Invoice adjInv = buildInvoice(1L, typeAdj);
        adjInv.setAutoMatching(true);
        AccountOperation adjAo = buildAccountOperation(1L);

        // Original invoice  data : Invoice, Type, AO
        InvoiceType typeCom = buildInvoiceType("COM");
        Invoice originalInv = buildInvoice(-1L, typeCom);
        AccountOperation originalAo = buildAccountOperation(2L);

        LinkedInvoice linkedInvoice = new LinkedInvoice();
        linkedInvoice.setInvoice(originalInv);
        linkedInvoice.setLinkedInvoiceValue(adjInv);

        Mockito.when(serviceSingleton.validateAndAssignInvoiceNumber(adjInv.getId(), true)).thenReturn(adjInv);
        Mockito.when(invoiceService.refreshOrRetrieve(Mockito.any(Invoice.class))).thenReturn(adjInv);
        Mockito.when(invoiceService.findById(adjInv.getId())).thenReturn(adjInv);
        Mockito.when(invoiceTypeService.getListAdjustementCode()).thenReturn(List.of("ADJ"));

        Mockito.when(invoiceService.findBySourceInvoiceByAdjId(adjInv.getId())).thenReturn(null);

        try {
            invoiceApi.validateInvoice(adjInv.getId(), false, true, true);
            Assert.fail("No linked invoice found exception must be thrown");
        } catch (Exception e) {
            Assert.assertEquals("Adjustment invoice [" + adjInv.getId() + "] does not have a link with a source Invoice", e.getMessage());
        }
    }

    private AccountOperation buildAccountOperation(long id) {
        AccountOperation adjAo = new AccountOperation();
        adjAo.setId(id);
        adjAo.setUnMatchingAmount(BigDecimal.TEN);
        adjAo.setMatchingStatus(MatchingStatusEnum.O);
        adjAo.setCustomerAccount(new CustomerAccount());
        return adjAo;
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